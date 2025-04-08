package org.sunshine.security.jwt.core;

import java.util.Map;

/**
 * @author Teamo
 * @since 2025/4/8
 */
public class GenericJwtToken extends AbstractToken {

    protected GenericJwtToken(Map<String, Object> header, Map<String, Object> claims) {
        super(header, claims);
    }

    public static GenericJwtTokenBuilder withTokenType(JwtTokenType tokenType) {
        return new GenericJwtTokenBuilder(tokenType);
    }

    public static class GenericJwtTokenBuilder extends Builder<GenericJwtToken> {

        protected GenericJwtTokenBuilder(JwtTokenType tokenType) {
            super(tokenType);
        }

        @Override
        public GenericJwtToken build() {
            validate();
            return new GenericJwtToken(header, claims);
        }
    }
}
