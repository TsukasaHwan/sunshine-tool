package org.sunshine.core.cache.exception;

/**
 * @author Teamo
 * @since 2025/3/19
 */
public class RateLimitExceededException extends RuntimeException {

    /**
     * Creates a new instance with the specified explanation message and underlying cause.
     *
     * @param msg   the message explaining why the exception is thrown.
     * @param cause the underlying cause that resulted in this exception being thrown.
     */
    public RateLimitExceededException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Creates a new instance with the specified explanation message.
     *
     * @param msg the message explaining why the exception is thrown.
     */
    public RateLimitExceededException(String msg) {
        super(msg);
    }
}
