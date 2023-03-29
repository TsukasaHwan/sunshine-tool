package org.sunshine.core.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * JwtToken过期异常
 *
 * @author Teamo
 * @since 2023/3/16
 */
public class JwtExpiredException extends AuthenticationException {

    public JwtExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtExpiredException(String msg) {
        super(msg);
    }
}
