package org.sunshine.security.jwt.filter;

import io.jsonwebtoken.Claims;
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
import org.sunshine.security.core.util.SecurityUtils;
import org.sunshine.security.jwt.exception.ExpiredJwtAuthenticationException;
import org.sunshine.security.jwt.exception.JwtAuthenticationException;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.util.JwtClaimsUtils;

import java.io.IOException;

/**
 * JwtToken拦截器
 *
 * @author Teamo
 * @since 2023/3/13
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UserDetailsService userDetailsService;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final JwtSecurityProperties properties;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, AuthenticationFailureHandler authenticationFailureHandler, JwtSecurityProperties properties) {
        this.userDetailsService = userDetailsService;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.properties = properties;
    }

    @Override
    protected void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = JwtClaimsUtils.getToken(request);

        if (authToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = JwtClaimsUtils.parseToken(authToken);

            String refreshTokenClaim = claims.get(JwtClaimsUtils.REFRESH_TOKEN_CLAIM_KEY, String.class);
            if (refreshTokenClaim == null && request.getServletPath().contains(properties.getRefreshTokenPath())) {
                filterChain.doFilter(request, response);
                return;
            }

            if (refreshTokenClaim != null && refreshTokenClaim.equals(properties.getRefreshTokenClaim())) {
                if (request.getServletPath().equals(properties.getRefreshTokenPath())) {
                    doAuthenticate(request, authToken, claims);
                }
                filterChain.doFilter(request, response);
                return;
            }

            doAuthenticate(request, authToken, claims);
        } catch (Exception e) {
            unsuccessfulAuthentication(request, response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 认证
     *
     * @param request   HttpServletRequest
     * @param authToken JWT
     * @param claims    Claims
     */
    private void doAuthenticate(HttpServletRequest request, String authToken, Claims claims) {
        String username = claims.getSubject();
        if (username != null && SecurityUtils.getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (JwtClaimsUtils.validateToken(authToken, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityUtils.setAuthentication(authentication);
            }
        }
    }

    private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        SecurityUtils.clearContext();
        AuthenticationException exception;
        if (e instanceof JwtException) {
            exception = handleJwtException((JwtException) e);
        } else if (e instanceof AuthenticationException) {
            exception = (AuthenticationException) e;
        } else {
            log.error(e.getMessage(), e);
            exception = new JwtAuthenticationException(e.getMessage());
        }
        authenticationFailureHandler.onAuthenticationFailure(request, response, exception);
    }

    /**
     * 将JwtException包装为AuthenticationException
     *
     * @param e {@link JwtException}
     * @return {@link AuthenticationException}
     */
    private AuthenticationException handleJwtException(JwtException e) {
        if (e instanceof ExpiredJwtException) {
            return new ExpiredJwtAuthenticationException(e.getMessage());
        } else {
            log.error(e.getMessage(), e);
            return new JwtAuthenticationException(e.getMessage());
        }
    }
}
