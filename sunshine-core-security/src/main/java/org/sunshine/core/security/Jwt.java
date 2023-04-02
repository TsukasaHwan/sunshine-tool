package org.sunshine.core.security;

import java.io.Serializable;

/**
 * @author Teamo
 * @since 2023/4/2
 */
public class Jwt implements Serializable {
    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    public Jwt() {
    }

    public Jwt(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
