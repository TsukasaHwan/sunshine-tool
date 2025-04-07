package org.sunshine.security.jwt.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.sunshine.security.jwt.JwtToken;
import org.sunshine.security.jwt.JwtTokenType;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;

/**
 * @author Teamo
 * @since 2025/4/7
 */
public class AccessTokenAuthenticator extends AbstractTokenAuthenticator {

    public AccessTokenAuthenticator(JwtSecurityProperties jwtSecurityProperties, UserDetailsService userDetailsService) {
        super(jwtSecurityProperties, userDetailsService);
    }

    @Override
    public JwtTokenType getTokenType() {
        return JwtTokenType.ACCESS_TOKEN;
    }

    @Override
    public void authenticate(HttpServletRequest request, JwtToken token) throws Exception {
        if (isRefreshPath(request)) {
            return;
        }
        doAuthenticate(request, token);
    }
}
