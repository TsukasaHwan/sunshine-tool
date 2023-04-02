package org.sunshine.core.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.sunshine.core.security.exception.JwtExpiredException;
import org.sunshine.core.security.properties.JwtSecurityProperties;
import org.sunshine.core.security.util.JwtClaimsUtils;
import org.sunshine.core.security.util.SecurityUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * JwtToken拦截器
 *
 * @author Teamo
 * @since 2023/3/13
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UserDetailsService userDetailsService;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final JwtSecurityProperties properties;

    private Set<String> permitAllAntPatterns;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, AuthenticationFailureHandler authenticationFailureHandler, JwtSecurityProperties properties) {
        this.userDetailsService = userDetailsService;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isPermitAll(request)) {
            filterChain.doFilter(request, response);
            return;
        }

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
                    authenticate(request, authToken, claims);
                }
                filterChain.doFilter(request, response);
                return;
            }

            authenticate(request, authToken, claims);
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
    private void authenticate(HttpServletRequest request, String authToken, Claims claims) {
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
        AuthenticationException exception = null;
        if (e instanceof JwtException) {
            exception = handleJwtException((JwtException) e);
        } else if (e instanceof AuthenticationException) {
            exception = (AuthenticationException) e;
        } else {
            log.error(e.getMessage(), e);
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
            return new JwtExpiredException(e.getMessage());
        } else {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断请求路径是否包含@PermitAll注解
     *
     * @param request HttpServletRequest
     * @return 是否需要token认证
     */
    private boolean isPermitAll(HttpServletRequest request) {
        boolean isPermitAll = false;
        if (permitAllAntPatterns != null && !permitAllAntPatterns.isEmpty()) {
            if (permitAllAntPatterns.contains(request.getServletPath())) {
                isPermitAll = true;
            }
        }
        return isPermitAll;
    }

    public Set<String> getPermitAllAntPatterns() {
        return permitAllAntPatterns;
    }

    public void setPermitAllAntPatterns(Set<String> permitAllAntPatterns) {
        this.permitAllAntPatterns = permitAllAntPatterns;
    }
}
