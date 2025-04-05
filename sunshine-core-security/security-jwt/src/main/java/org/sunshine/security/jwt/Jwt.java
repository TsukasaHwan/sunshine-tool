package org.sunshine.security.jwt;

import org.sunshine.security.jwt.util.JwtUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Teamo
 * @since 2023/4/2
 */
public class Jwt implements Serializable {

    private final String accessToken;

    private final String refreshToken;

    private final Long expiresIn;

    Jwt(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Jwt of(String subject) {
        return of(subject, null);
    }

    public static Jwt of(String subject, Map<String, ?> claims) {
        String accessToken = JwtUtils.accessToken(subject, claims);
        String refreshToken = JwtUtils.refreshToken(subject);
        long time = JwtUtils.parseToken(accessToken).getExpiration().getTime();
        return builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(time)
                .build();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public static class Builder {
        private String accessToken;

        private String refreshToken;

        private Long expiresIn;

        Builder() {
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Jwt build() {
            return new Jwt(this.accessToken, this.refreshToken, this.expiresIn);
        }
    }
}
