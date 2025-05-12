package com.allomed.app.aop.ratelimit;

// Local imports
import com.allomed.app.security.AuthoritiesConstants;
// Bucket4j imports
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
// Jakarta Servlet imports (for Spring Boot 3+)
import jakarta.servlet.http.HttpServletRequest;
// Java core imports
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
// AspectJ imports
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
// SLF4J (Logging) imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Spring imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken; // Ensure this is imported
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser; // <<< Ensure this is imported
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken; // Ensure this is imported
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

/**
 * Aspect for handling rate limiting on methods annotated with @RateLimited.
 * Uses Bucket4j with Redis backend, applying limits based on user roles.
 */
@Aspect
@Component
public class RateLimitingAspect {

    private final Logger log = LoggerFactory.getLogger(RateLimitingAspect.class);
    private static final String RATE_LIMIT_PREFIX = "rate_limit";

    // Inject the Bucket4j proxy manager configured in RateLimitingConfiguration
    @Autowired
    private ProxyManager<String> proxyManager;

    // --- Define Limits ---
    // TODO: Consider moving these to application properties for better configuration
    private static final long APP_USER_LIMIT_PER_DAY = 250;
    private static final long GUEST_LIMIT_PER_DAY = 5;
    private static final Duration LIMIT_DURATION = Duration.ofDays(1); // Daily limits

    /**
     * Intercepts methods annotated with @RateLimited.
     */
    @Around("@annotation(com.allomed.app.aop.ratelimit.RateLimited)")
    public Object rateLimitEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // --- DETAILED AUTHENTICATION LOGGING --- // (Kept for debugging if needed)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("--- RateLimitingAspect Start ---");
        log.info("Method: {}", method.getName());
        if (authentication != null) {
            log.info("Authentication object PRESENT.");
            log.info("Authentication TYPE: {}", authentication.getClass().getName());
            log.info("Is Authenticated?: {}", authentication.isAuthenticated());
            log.info("Authorities: {}", authentication.getAuthorities());
            if (authentication.getPrincipal() != null) {
                log.info("Principal TYPE: {}", authentication.getPrincipal().getClass().getName());
            } else {
                log.info("Principal is NULL.");
            }
            if (authentication instanceof JwtAuthenticationToken) {
                log.info("Object IS instanceof JwtAuthenticationToken.");
            } else {
                log.info("Object IS NOT instanceof JwtAuthenticationToken.");
            }
            if (authentication instanceof AnonymousAuthenticationToken) {
                log.info("Object IS instanceof AnonymousAuthenticationToken.");
            }
        } else {
            log.info("Authentication object is NULL.");
        }
        log.info("--- RateLimitingAspect Auth Check End ---");
        // --- END OF DETAILED LOGGING --- //

        // --- Corrected Logic ---
        Set<String> roles = getRoles(authentication);

        // 1. Bypass for ADMIN
        if (roles.contains(AuthoritiesConstants.ADMIN)) {
            log.trace("ADMIN bypass for: {}", method.getName());
            return joinPoint.proceed(); // Execute the original method without limiting
        }

        // 2. Determine Limit and Identifier
        Bandwidth bandwidth;
        String identifier;
        String contextLog;

        if (roles.contains(AuthoritiesConstants.APP_USER) || roles.contains(AuthoritiesConstants.USER)) {
            // --- APP_USER / USER ---
            // Call the UPDATED getUserIdFromJwt method
            Optional<String> userIdOpt = getUserIdFromJwt(authentication);
            if (userIdOpt.isEmpty()) {
                // This should NOT happen now if logged in via Keycloak or Bearer token
                log.error(
                    "Could not extract User ID ('sub') for authenticated user even after update. AuthType: {}. Denying {}",
                    (authentication != null ? authentication.getClass().getName() : "null"),
                    method.getName()
                );
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User ID missing for rate limit.");
            }
            identifier = userIdOpt.get();
            bandwidth = Bandwidth.classic(APP_USER_LIMIT_PER_DAY, Refill.intervally(APP_USER_LIMIT_PER_DAY, LIMIT_DURATION));
            contextLog = "APP_USER(ID:" + identifier.substring(0, Math.min(identifier.length(), 8)) + "...)";
        } else if (roles.contains(AuthoritiesConstants.ANONYMOUS) || !SecurityUtils.isAuthenticated(authentication)) {
            // --- GUEST ---
            identifier = getClientIpAddress();
            if (identifier == null || identifier.isBlank()) {
                log.error("Cannot determine client IP for anonymous rate limit. Denying {}", method.getName());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Client ID missing for rate limit.");
            }
            bandwidth = Bandwidth.classic(GUEST_LIMIT_PER_DAY, Refill.intervally(GUEST_LIMIT_PER_DAY, LIMIT_DURATION));
            contextLog = "GUEST(IP:" + identifier + ")";
        } else {
            // --- UNHANDLED / FORBIDDEN --- (e.g., DOCTOR caught here)
            log.warn("Unhandled roles {} for rate limiting {}. Denying.", roles, method.getName());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied or rate limit not configured.");
        }

        // 3. Build Redis Key
        String redisKey = buildRedisKey(method, identifier);
        log.trace("Checking rate limit for {} on key: {}", contextLog, redisKey);

        // 4. Configure Bucket4j Bucket
        BucketConfiguration configuration = BucketConfiguration.builder().addLimit(bandwidth).build();
        Supplier<BucketConfiguration> configSupplier = () -> configuration;
        Bucket bucket = proxyManager.builder().build(redisKey, configSupplier);

        // 5. Attempt to consume a token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // 6. Check result and proceed or reject
        if (probe.isConsumed()) {
            log.trace("Rate limit check passed for key {}. Remaining: {}", redisKey, probe.getRemainingTokens());
            return joinPoint.proceed();
        } else {
            long millisToWaitForRefill = TimeUnit.NANOSECONDS.toMillis(probe.getNanosToWaitForRefill());
            log.warn("Rate limit EXCEEDED for {} on key {}. Wait {} ms.", contextLog, redisKey, millisToWaitForRefill);
            addRetryAfterHeaderToResponse(millisToWaitForRefill);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Please try again later.");
        }
    }

    // --- Helper Methods ---

    private String buildRedisKey(Method method, String identifier) {
        return RATE_LIMIT_PREFIX + ":" + method.getDeclaringClass().getSimpleName() + ":" + method.getName() + ":" + identifier;
    }

    private Set<String> getRoles(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Set.of(AuthoritiesConstants.ANONYMOUS);
        }
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    // --- UPDATED Method to handle both JWT and OIDC User ---
    private Optional<String> getUserIdFromJwt(Authentication authentication) {
        if (authentication == null) {
            log.warn("getUserIdFromAuthentication called with NULL authentication object.");
            return Optional.empty();
        }

        String userId = null;

        // Case 1: JWT Authentication (Bearer Token)
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            log.trace("Authentication is JwtAuthenticationToken.");
            Jwt jwt = jwtAuth.getToken();
            if (jwt == null) {
                log.warn("JwtAuthenticationToken contains a NULL JWT token.");
                return Optional.empty();
            }
            userId = jwt.getSubject(); // 'sub' claim from JWT
            log.trace("Extracted 'sub' claim via JwtAuthenticationToken: {}", userId);
        }
        // Case 2: OAuth2 Login Authentication (OIDC User principal)
        else if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            // Check the Authentication type as well for clarity, though checking Principal is key
            log.trace(
                "Authentication principal is OidcUser (likely from OAuth2AuthenticationToken). Type: {}",
                authentication.getClass().getName()
            );
            userId = oidcUser.getSubject(); // 'sub' claim from OidcUser
            log.trace("Extracted 'sub' claim via OidcUser principal: {}", userId);
        }

        // If neither case matched or extraction failed, log a warning
        if (userId == null) {
            log.warn(
                "getUserIdFromAuthentication: Could not extract 'sub' claim. Authentication type: {}, Principal type: {}",
                authentication.getClass().getName(),
                (authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null")
            );
            return Optional.empty();
        }

        // Check if extracted ID is valid
        if (userId.isBlank()) {
            log.warn("Extracted 'sub' claim is null or blank.");
            return Optional.empty();
        }

        return Optional.of(userId);
    }

    // --- End of UPDATED Method ---

    private String getClientIpAddress() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            log.error("Cannot get ServletRequestAttributes in RateLimitingAspect.");
            return null;
        }
        HttpServletRequest request = sra.getRequest();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private void addRetryAfterHeaderToResponse(long millisToWait) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null && sra.getResponse() != null) {
            long secondsToWait = Math.max(1, TimeUnit.MILLISECONDS.toSeconds(millisToWait));
            sra.getResponse().addHeader("Retry-After", String.valueOf(secondsToWait));
            log.trace("Adding Retry-After header: {}", secondsToWait);
        } else {
            log.warn("Cannot add Retry-After header, response context not available.");
        }
    }

    // Static inner class (keep as is)
    private static class SecurityUtils {

        static boolean isAuthenticated(Authentication authentication) {
            return (
                authentication != null &&
                authentication.isAuthenticated() &&
                authentication
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .noneMatch(AuthoritiesConstants.ANONYMOUS::equals)
            );
        }
    }
}
