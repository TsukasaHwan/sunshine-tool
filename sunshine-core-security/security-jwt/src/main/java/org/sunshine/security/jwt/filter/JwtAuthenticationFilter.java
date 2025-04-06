package org.sunshine.security.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.sunshine.security.core.filter.AbstractAuthenticationFilter;
import org.sunshine.security.core.support.PathPatternRequestMatcher;
import org.sunshine.security.core.util.SecurityUtils;
import org.sunshine.security.jwt.JwtToken;
import org.sunshine.security.jwt.JwtTokenType;
import org.sunshine.security.jwt.exception.ExpiredJwtAuthenticationException;
import org.sunshine.security.jwt.exception.JwtAuthenticationException;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
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

    private final PathPatternRequestMatcher pathPatternRequestMatcher;

    private final UserDetailsService userDetailsService;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final JwtSecurityProperties properties;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, AuthenticationFailureHandler authenticationFailureHandler, JwtSecurityProperties properties) {
        this.userDetailsService = userDetailsService;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.properties = properties;
        this.pathPatternRequestMatcher = properties.getRefreshTokenPath() == null ?
                null : PathPatternRequestMatcher.withDefaults().matcher(properties.getRefreshTokenPath());
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

            boolean enabledAnnotation = Boolean.TRUE.equals(properties.getEnabledRefreshTokenApiAnnotation());
            if (!enabledAnnotation) {
                if (tokenType.equals(JwtTokenType.REFRESH_TOKEN)) {
                    if (isRefreshPath(request)) {
                        doAuthenticate(request, jwtToken);
                    }
                    filterChain.doFilter(request, response);
                    return;
                }

                if (isRefreshPath(request)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            doAuthenticate(request, jwtToken);
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
     * 认证
     *
     * @param request  HttpServletRequest
     * @param jwtToken JwtToken
     */
    private void doAuthenticate(HttpServletRequest request, JwtToken jwtToken) {
        String authToken = jwtToken.getTokenValue();
        String username = jwtToken.getClaims().getSubject();
        if (username != null && SecurityUtils.getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (JwtUtils.validateToken(authToken, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityUtils.setAuthentication(authentication);
            }
        }
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

    private boolean isRefreshPath(HttpServletRequest request) {
        return this.pathPatternRequestMatcher != null && this.pathPatternRequestMatcher.matches(request);
    }
}
