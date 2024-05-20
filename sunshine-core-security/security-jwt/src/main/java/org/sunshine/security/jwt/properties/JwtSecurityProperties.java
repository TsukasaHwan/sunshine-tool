package org.sunshine.security.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * @author Teamo
 * @since 2023/3/14
 */
@ConfigurationProperties("jwt.security")
public class JwtSecurityProperties {

    /**
     * Token Custom Request Header.
     */
    private String header = HttpHeaders.AUTHORIZATION;

    /**
     * Refresh token claim
     */
    private String refreshTokenClaim = "refresh_token";

    /**
     * Token expiration time (default 30 minutes).
     */
    private Duration expiresIn = Duration.ofMinutes(30L);

    /**
     * RefreshToken expiration time (default 1 hours).
     */
    private Duration refreshTokenExpiresIn = Duration.ofHours(1L);

    /**
     * If the clock of the machine generating the token has a reasonable drift from the machine parsing it, the expiration check may of.
     * In this case you can use this field to allow some wiggle room on the difference between the clocks (1 to 2 minutes should be more than enough, Default is 0)
     */
    private Duration allowedClockSkew = Duration.ofSeconds(0L);

    /**
     * Accessible resource path.
     */
    private List<String> permitAllPaths = Arrays.asList(
            "/",
            "/error",
            "/*.html",
            "/*/*.html",
            "/*/*.css",
            "/*/*.js",
            "/profile/**",
            "/favicon.ico",
            "/swagger-resources/**",
            "/webjars/**",
            "/*/api-docs/**"
    );

    /**
     * Logout resource path.
     */
    private String logoutPath = "/logout";

    /**
     * RefreshToken resource path.
     */
    private String refreshTokenPath;

    /**
     * secret
     */
    private Secret secret = new Secret();

    public static class Secret {
        /**
         * RSA public key
         */
        private RSAPublicKey publicKey;

        /**
         * RSA Private Key
         */
        private RSAPrivateKey privateKey;

        public RSAPublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(RSAPublicKey publicKey) {
            this.publicKey = publicKey;
        }

        public RSAPrivateKey getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(RSAPrivateKey privateKey) {
            this.privateKey = privateKey;
        }
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getRefreshTokenClaim() {
        return refreshTokenClaim;
    }

    public void setRefreshTokenClaim(String refreshTokenClaim) {
        this.refreshTokenClaim = refreshTokenClaim;
    }

    public Duration getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Duration expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Duration getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public void setRefreshTokenExpiresIn(Duration refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public Duration getAllowedClockSkew() {
        return allowedClockSkew;
    }

    public void setAllowedClockSkew(Duration allowedClockSkew) {
        this.allowedClockSkew = allowedClockSkew;
    }

    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }

    public String getLogoutPath() {
        return logoutPath;
    }

    public void setLogoutPath(String logoutPath) {
        this.logoutPath = logoutPath;
    }

    public String getRefreshTokenPath() {
        return refreshTokenPath;
    }

    public void setRefreshTokenPath(String refreshTokenPath) {
        this.refreshTokenPath = refreshTokenPath;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }
}
