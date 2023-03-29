package org.sunshine.core.cache.exception;

/**
 * @author Teamo
 * @since 2023/3/28
 */
public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);
    }

    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
