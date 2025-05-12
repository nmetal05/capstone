package com.allomed.app.aop.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to apply role-based rate limiting via RateLimitingAspect.
 * Place this on controller methods that should be rate-limited.
 */
@Target(ElementType.METHOD) // Annotation can only be applied to methods
@Retention(RetentionPolicy.RUNTIME) // Annotation metadata is available at runtime (needed for AOP)
public @interface RateLimited {
// This is just a marker, so it has no parameters.
// The actual rate limiting logic based on roles is in RateLimitingAspect.
}
