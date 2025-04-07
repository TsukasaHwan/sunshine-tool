package org.sunshine.security.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.sunshine.security.core.filter.AbstractAuthenticationFilter;
import org.sunshine.security.core.util.SecurityUtils;
import org.sunshine.security.jwt.JwtToken;
import org.sunshine.security.jwt.JwtTokenType;
import org.sunshine.security.jwt.authenticator.AbstractTokenAuthenticator;
import org.sunshine.security.jwt.authenticator.TokenAuthenticatorRegistry;
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

    private final AuthenticationFailureHandler authenticationFailureHandler;

    public JwtAuthenticationFilter(TokenAuthenticatorRegistry tokenAuthenticatorRegistry, AuthenticationFailureHandler authenticationFailureHandler) {
        this.tokenAuthenticatorRegistry = tokenAuthenticatorRegistry;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    protected void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = JwtUtils.getToken(request);

        if (authToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtToken jwtToken = JwtUtils.getJwtToken(request, authToken);
            JwtTokenType tokenType = jwtToken.getTokenType();

            if (tokenType == null) {
                filterChain.doFilter(request, response);
                return;
            }

            AbstractTokenAuthenticator authenticator = this.tokenAuthenticatorRegistry.getTokenAuthenticator(tokenType);
            authenticator.authenticate(request, jwtToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            unsuccessfulAuthentication(request, response, e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        SecurityUtils.clearContext();
        AuthenticationException exception = convertException(e);
        authenticationFailureHandler.onAuthenticationFailure(request, response, exception);
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
