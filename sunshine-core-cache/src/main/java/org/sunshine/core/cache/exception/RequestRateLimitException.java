package org.sunshine.core.cache.exception;

/**
 * @author Teamo
 * @since 2023/3/28
 */
public class RequestRateLimitException extends RuntimeException {

    public RequestRateLimitException(String message) {
        super(message);
    }

    public RequestRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
