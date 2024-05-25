package org.sunshine.security.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception indicating that a JWT was not correctly constructed and should be rejected.
 *
 * @author Teamo
 * @since 2024/5/25
 */
public class JwtAuthenticationException extends AuthenticationException {

    /**
     * Creates a new instance with the specified explanation message and underlying cause.
     *
     * @param msg   the message explaining why the exception is thrown.
     * @param cause the underlying cause that resulted in this exception being thrown.
     */
    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Creates a new instance with the specified explanation message.
     *
     * @param msg the message explaining why the exception is thrown.
     */
    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
