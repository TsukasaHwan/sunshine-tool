package org.sunshine.security.jwt.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.sunshine.security.core.support.PathPatternRequestMatcher;
import org.sunshine.security.core.util.SecurityUtils;
import org.sunshine.security.jwt.JwtToken;
import org.sunshine.security.jwt.JwtTokenType;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.util.JwtUtils;

import java.util.Optional;

/**
 * @author Teamo
 * @since 2025/4/7
 */
public abstract class AbstractTokenAuthenticator {

    protected final JwtSecurityProperties jwtSecurityProperties;

    protected final UserDetailsService userDetailsService;

    private final PathPatternRequestMatcher pathPatternRequestMatcher;

    protected AbstractTokenAuthenticator(JwtSecurityProperties jwtSecurityProperties, UserDetailsService userDetailsService) {
        this.jwtSecurityProperties = jwtSecurityProperties;
        this.userDetailsService = userDetailsService;
        this.pathPatternRequestMatcher = createPathMatcher(jwtSecurityProperties);
    }

    public abstract JwtTokenType getTokenType();

    public abstract void authenticate(HttpServletRequest request, JwtToken token) throws Exception;

    protected boolean isRefreshPath(HttpServletRequest request) {
        return this.pathPatternRequestMatcher != null &&
               this.pathPatternRequestMatcher.matches(request);
    }

    protected void doAuthenticate(HttpServletRequest request, JwtToken jwtToken) {
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

    private PathPatternRequestMatcher createPathMatcher(JwtSecurityProperties jwtSecurityProperties) {
        return Optional.ofNullable(jwtSecurityProperties.getRefreshTokenPath())
                .map(path -> PathPatternRequestMatcher.withDefaults().matcher(path))
                .orElse(null);
    }
}
