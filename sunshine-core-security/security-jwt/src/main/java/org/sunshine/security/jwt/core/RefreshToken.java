package org.sunshine.security.jwt.core;

import java.util.Map;

/**
 * @author Teamo
 * @since 2025/4/8
 */
public class RefreshToken extends AbstractToken {

    protected RefreshToken(Map<String, Object> header, Map<String, Object> claims) {
        super(header, claims);
    }

    public static RefreshTokenBuilder builder() {
        return new RefreshTokenBuilder();
    }

    public static class RefreshTokenBuilder extends Builder<RefreshToken> {
        RefreshTokenBuilder() {
            super(JwtTokenType.REFRESH_TOKEN);
        }

        @Override
        public RefreshToken build() {
            validate();
            return new RefreshToken(header, claims);
        }
    }
}
