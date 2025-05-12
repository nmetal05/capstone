package com.allomed.app.web.rest.errors;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
// Import Logger explicitly if needed, though usually inherited logger is sufficient
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException; // Ensure imported
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;
import tech.jhipster.web.util.HeaderUtil;

@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    private static final String FIELD_ERRORS_KEY = "fieldErrors";
    private static final String MESSAGE_KEY = "message";
    private static final String PATH_KEY = "path";
    private static final boolean CASUAL_CHAIN_ENABLED = false;

    // Use the logger defined by ResponseEntityExceptionHandler or declare one if needed
    // private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class); // Already inherited

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Environment env;

    public ExceptionTranslator(Environment env) {
        super();
        this.env = env;
    }

    // Keep handleAnyException and other generic handlers
    @ExceptionHandler
    public ResponseEntity<Object> handleAnyException(Throwable ex, NativeWebRequest request) {
        // Only log unexpected exceptions here if desired, or let specific handlers control logging
        // logger.error("Unhandled exception caught:", ex); // Example of logging only truly unhandled ones
        ProblemDetailWithCause pdCause = wrapAndCustomizeProblem(ex, request);
        HttpHeaders headers = Optional.ofNullable(buildHeaders(ex)).orElse(new HttpHeaders());
        return handleExceptionInternal((Exception) ex, pdCause, headers, HttpStatusCode.valueOf(pdCause.getStatus()), request);
    }

    @Nullable
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception ex,
        @Nullable Object body,
        HttpHeaders headers,
        HttpStatusCode statusCode,
        WebRequest request
    ) {
        body = body == null ? wrapAndCustomizeProblem((Throwable) ex, (NativeWebRequest) request) : body;
        headers = Optional.ofNullable(headers).orElse(new HttpHeaders());
        // The base class might do its own logging here (often WARN level)
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    // Keep all the helper methods (wrapAndCustomizeProblem, getProblemDetailWithCause, customizeProblem, etc.)
    // including the modifications made previously for AuthorizationDeniedException in:
    // getMappedStatus, getMappedMessageKey, getCustomizedTitle, getCustomizedErrorDetails

    protected ProblemDetailWithCause wrapAndCustomizeProblem(Throwable ex, NativeWebRequest request) {
        return customizeProblem(getProblemDetailWithCause(ex), ex, request);
    }

    private ProblemDetailWithCause getProblemDetailWithCause(Throwable ex) {
        if (ex instanceof ErrorResponseException exp && exp.getBody() instanceof ProblemDetailWithCause pdwc) return pdwc;
        return ProblemDetailWithCauseBuilder.instance().withStatus(toStatus(ex).value()).build();
    }

    protected ProblemDetailWithCause customizeProblem(ProblemDetailWithCause problem, Throwable err, NativeWebRequest request) {
        if (problem.getStatus() <= 0) problem.setStatus(toStatus(err));
        if (problem.getType() == null || problem.getType().equals(URI.create("about:blank"))) problem.setType(getMappedType(err));
        String title = extractTitle(err, problem.getStatus());
        String problemTitle = problem.getTitle();
        if (problemTitle == null || !problemTitle.equals(title)) problem.setTitle(title);
        if (problem.getDetail() == null) problem.setDetail(getCustomizedErrorDetails(err));
        Map<String, Object> problemProperties = problem.getProperties();
        if (problemProperties == null || !problemProperties.containsKey(MESSAGE_KEY)) problem.setProperty(
            MESSAGE_KEY,
            getMappedMessageKey(err) != null ? getMappedMessageKey(err) : "error.http." + problem.getStatus()
        );
        if (problemProperties == null || !problemProperties.containsKey(PATH_KEY)) problem.setProperty(PATH_KEY, getPathValue(request));
        if (
            (err instanceof MethodArgumentNotValidException fieldException) &&
            (problemProperties == null || !problemProperties.containsKey(FIELD_ERRORS_KEY))
        ) problem.setProperty(FIELD_ERRORS_KEY, getFieldErrors(fieldException));
        problem.setCause(buildCause(err.getCause(), request).orElse(null));
        return problem;
    }

    private String extractTitle(Throwable err, int statusCode) {
        return getCustomizedTitle(err) != null ? getCustomizedTitle(err) : extractTitleForResponseStatus(err, statusCode);
    }

    private List<FieldErrorVM> getFieldErrors(MethodArgumentNotValidException ex) {
        return ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(f ->
                new FieldErrorVM(
                    f.getObjectName().replaceFirst("DTO$", ""),
                    f.getField(),
                    StringUtils.isNotBlank(f.getDefaultMessage()) ? f.getDefaultMessage() : f.getCode()
                )
            )
            .toList();
    }

    private String extractTitleForResponseStatus(Throwable err, int statusCode) {
        ResponseStatus specialStatus = extractResponseStatus(err);
        return specialStatus == null ? HttpStatus.valueOf(statusCode).getReasonPhrase() : specialStatus.reason();
    }

    private String extractURI(NativeWebRequest request) {
        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        return nativeRequest != null ? nativeRequest.getRequestURI() : StringUtils.EMPTY;
    }

    private HttpStatus toStatus(final Throwable throwable) {
        if (throwable instanceof ErrorResponse err) return HttpStatus.valueOf(err.getBody().getStatus());
        return Optional.ofNullable(getMappedStatus(throwable)).orElse(
            Optional.ofNullable(resolveResponseStatus(throwable)).map(ResponseStatus::value).orElse(HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    private ResponseStatus extractResponseStatus(final Throwable throwable) {
        return Optional.ofNullable(resolveResponseStatus(throwable)).orElse(null);
    }

    private ResponseStatus resolveResponseStatus(final Throwable type) {
        final ResponseStatus candidate = findMergedAnnotation(type.getClass(), ResponseStatus.class);
        return candidate == null && type.getCause() != null ? resolveResponseStatus(type.getCause()) : candidate;
    }

    private URI getMappedType(Throwable err) {
        if (err instanceof MethodArgumentNotValidException) return ErrorConstants.CONSTRAINT_VIOLATION_TYPE;
        return ErrorConstants.DEFAULT_TYPE;
    }

    private String getMappedMessageKey(Throwable err) {
        if (err instanceof MethodArgumentNotValidException) return ErrorConstants.ERR_VALIDATION;
        if (
            err instanceof ConcurrencyFailureException || err.getCause() instanceof ConcurrencyFailureException
        ) return ErrorConstants.ERR_CONCURRENCY_FAILURE;
        if (err instanceof AuthorizationDeniedException || err instanceof AccessDeniedException) return "error.http.403";
        return null;
    }

    private String getCustomizedTitle(Throwable err) {
        if (err instanceof MethodArgumentNotValidException) return "Method argument not valid";
        if (err instanceof AuthorizationDeniedException || err instanceof AccessDeniedException) return "Access Denied";
        return null;
    }

    private String getCustomizedErrorDetails(Throwable err) {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            if (err instanceof HttpMessageConversionException) return "Unable to convert http message";
            if (err instanceof DataAccessException) return "Failure during data access";
            if (containsPackageName(err.getMessage())) return "Unexpected runtime exception";
        }
        if (
            err instanceof AuthorizationDeniedException || err instanceof AccessDeniedException
        ) return "You do not have the required permissions to perform this action.";
        return err.getCause() != null ? err.getCause().getMessage() : err.getMessage();
    }

    private HttpStatus getMappedStatus(Throwable err) {
        if (err instanceof AccessDeniedException || err instanceof AuthorizationDeniedException) return HttpStatus.FORBIDDEN;
        if (err instanceof ConcurrencyFailureException) return HttpStatus.CONFLICT;
        if (err instanceof BadCredentialsException) return HttpStatus.UNAUTHORIZED;
        if (err instanceof MethodArgumentNotValidException) return HttpStatus.BAD_REQUEST;
        return null;
    }

    private URI getPathValue(NativeWebRequest request) {
        if (request == null) return URI.create("about:blank");
        return URI.create(extractURI(request));
    }

    private HttpHeaders buildHeaders(Throwable err) {
        return err instanceof BadRequestAlertException badRequestAlertException
            ? HeaderUtil.createFailureAlert(
                applicationName,
                true,
                badRequestAlertException.getEntityName(),
                badRequestAlertException.getErrorKey(),
                badRequestAlertException.getMessage()
            )
            : null;
    }

    public Optional<ProblemDetailWithCause> buildCause(final Throwable throwable, NativeWebRequest request) {
        if (throwable != null && isCasualChainEnabled()) {
            ProblemDetailWithCause causeProblem = getProblemDetailWithCause(throwable);
            return Optional.of(customizeProblem(causeProblem, throwable, request));
        }
        return Optional.ofNullable(null);
    }

    private boolean isCasualChainEnabled() {
        return CASUAL_CHAIN_ENABLED;
    }

    private boolean containsPackageName(String message) {
        return StringUtils.containsAny(message, "org.", "java.", "net.", "jakarta.", "javax.", "com.", "io.", "de.", "com.allomed.app");
    }

    // --- Specific Exception Handler for AuthorizationDeniedException ---
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex, NativeWebRequest request) {
        // Construct the full message first
        String logMessage = String.format("Authorization Denied for request [%s]: %s", extractURI(request), ex.getMessage());
        // Log the pre-formatted string using the single-argument warn method
        logger.warn(logMessage); // Use the inherited logger with a single String argument

        // Build the standard ProblemDetail using the existing helper methods
        ProblemDetailWithCause problem = ProblemDetailWithCauseBuilder.instance()
            .withStatus(HttpStatus.FORBIDDEN.value()) // 403
            .withTitle("Access Denied") // Consistent title
            .withDetail("You do not have the required permissions to perform this action.") // Consistent detail
            .withProperty(MESSAGE_KEY, "error.http.403") // Standard message key
            .withProperty(PATH_KEY, getPathValue(request)) // Consistent path
            .build();

        // Use the overridden handleExceptionInternal to build the final ResponseEntity
        return handleExceptionInternal(ex, problem, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
    // --- End of Specific Handler ---

}
