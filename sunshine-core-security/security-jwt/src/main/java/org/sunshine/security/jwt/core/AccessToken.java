package org.sunshine.security.jwt.core;

import java.util.Map;

/**
 * @author Teamo
 * @since 2025/4/8
 */
public class AccessToken extends AbstractToken {

    protected AccessToken(Map<String, Object> header, Map<String, Object> claims) {
        super(header, claims);
    }

    public static AccessTokenBuilder builder() {
        return new AccessTokenBuilder();
    }

    public static class AccessTokenBuilder extends Builder<AccessToken> {
        AccessTokenBuilder() {
            super(JwtTokenType.ACCESS_TOKEN);
        }

        @Override
        public AccessToken build() {
            return new AccessToken(this.header, this.claims);
        }
    }

}
