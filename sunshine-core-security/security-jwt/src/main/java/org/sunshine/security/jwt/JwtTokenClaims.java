package org.sunshine.security.jwt;

import io.jsonwebtoken.Claims;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Teamo
 * @since 2025/4/6
 */
public class JwtTokenClaims {

    private final Map<String, Object> claims;

    private JwtTokenClaims(Map<String, Object> claims) {
        this.claims = claims;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<String, Object> claims = new HashMap<>(16);

        Builder() {
        }

        public Builder subject(Object sub) {
            claims.put(Claims.SUBJECT, sub);
            return this;
        }

        public AccessTokenBuilder accessToken() {
            return new AccessTokenBuilder(claims);
        }

        public RefreshTokenBuilder refreshToken() {
            return new RefreshTokenBuilder(claims);
        }
    }

    public static class AccessTokenBuilder {

        private final Map<String, Object> claims;

        AccessTokenBuilder(Map<String, Object> claims) {
            claims.put(JwtClaimsNames.GRANT_TYPE, JwtTokenType.ACCESS_TOKEN);
            this.claims = claims;
        }

        public AccessTokenBuilder add(String name, Object value) {
            claims.put(name, value);
            return this;
        }

        public JwtTokenClaims build() {
            Object sub = claims.get(Claims.SUBJECT);
            if (sub == null || sub.toString().isBlank()) {
                throw new IllegalArgumentException("subject must not be null or blank");
            }
            return new JwtTokenClaims(claims);
        }
    }

    public static class RefreshTokenBuilder {

        private final Map<String, Object> claims;

        RefreshTokenBuilder(Map<String, Object> claims) {
            claims.put(JwtClaimsNames.GRANT_TYPE, JwtTokenType.REFRESH_TOKEN);
            this.claims = claims;
        }

        public RefreshTokenBuilder add(String name, Object value) {
            claims.put(name, value);
            return this;
        }

        public JwtTokenClaims build() {
            Object sub = claims.get(Claims.SUBJECT);
            if (sub == null || sub.toString().isBlank()) {
                throw new IllegalArgumentException("subject must not be null or blank");
            }
            return new JwtTokenClaims(claims);
        }
    }
}
