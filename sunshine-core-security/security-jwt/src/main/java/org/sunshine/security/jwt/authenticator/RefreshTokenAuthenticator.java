package org.sunshine.security.jwt.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.sunshine.security.jwt.core.JwtToken;
import org.sunshine.security.jwt.core.JwtTokenType;

/**
 * @author Teamo
 * @since 2025/4/7
 */
public class RefreshTokenAuthenticator extends AbstractTokenAuthenticator {

    public RefreshTokenAuthenticator(UserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    JwtTokenType getTokenType() {
        return JwtTokenType.REFRESH_TOKEN;
    }

    @Override
    public Authentication authenticate(HttpServletRequest request, JwtToken token) {
        if (!isRefreshPath(request)) {
            return null;
        }
        return doAuthenticate(request, token);
    }
}
