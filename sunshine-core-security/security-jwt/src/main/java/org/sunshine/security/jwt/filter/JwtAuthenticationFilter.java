package org.sunshine.security.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.sunshine.security.core.filter.AbstractAuthenticationFilter;
import org.sunshine.security.jwt.authenticator.AbstractTokenAuthenticator;
import org.sunshine.security.jwt.authenticator.TokenAuthenticatorRegistry;
import org.sunshine.security.jwt.core.JwtToken;
import org.sunshine.security.jwt.core.JwtTokenType;
import org.sunshine.security.jwt.exception.ExpiredJwtAuthenticationException;
import org.sunshine.security.jwt.exception.JwtAuthenticationException;
import org.sunshine.security.jwt.util.JwtUtils;

import java.io.IOException;

/**
 * JwtToken拦截器
 *
 * @author Teamo
 * @since 2023/3/13
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final TokenAuthenticatorRegistry tokenAuthenticatorRegistry;

    public JwtAuthenticationFilter(TokenAuthenticatorRegistry tokenAuthenticatorRegistry,
                                   AuthenticationSuccessHandler successHandler,
                                   AuthenticationFailureHandler failureHandler) {
        super(successHandler, failureHandler);
        this.tokenAuthenticatorRegistry = tokenAuthenticatorRegistry;
    }

    @Override
    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String authToken = JwtUtils.getToken(request);
        if (authToken == null) {
            return null;
        }

        JwtToken jwtToken = JwtUtils.getJwtToken(request, authToken);
        JwtTokenType tokenType = jwtToken.getTokenType();
        AbstractTokenAuthenticator authenticator = this.tokenAuthenticatorRegistry.getTokenAuthenticator(tokenType);
        return authenticator.authenticate(request, jwtToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        AuthenticationException exception = convertException(e);
        this.failureHandler.onAuthenticationFailure(request, response, exception);
    }

    /**
     * 转换为AuthenticationException
     *
     * @param e 异常
     * @return {@link AuthenticationException}
     */
    private AuthenticationException convertException(Exception e) {
        if (e instanceof ExpiredJwtException) {
            return new ExpiredJwtAuthenticationException(e.getMessage(), e);
        } else if (e instanceof JwtException) {
            return new JwtAuthenticationException(e.getMessage(), e);
        } else if (e instanceof AuthenticationException) {
            return (AuthenticationException) e;
        } else {
            log.error("Unexpected authentication error", e);
            return new JwtAuthenticationException("Authentication failed", e);
        }
    }
}
