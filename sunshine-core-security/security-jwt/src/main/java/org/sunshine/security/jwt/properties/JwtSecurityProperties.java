package org.sunshine.security.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.sunshine.security.jwt.annotation.RefreshTokenApi;

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
     * Token prefix.
     */
    private String tokenPrefix;

    /**
     * Token expiration time (default 30 minutes).
     */
    private Duration expiresIn = Duration.ofMinutes(30L);

    /**
     * If the clock of the machine generating the token has a reasonable drift from the machine parsing it, the expiration check may fail.
     * In this case you can use this field to allow some wiggle room on the difference between the clocks (1 to 2 minutes should be more than enough, Default is 0)
     */
    private Duration allowedClockSkew = Duration.ofSeconds(0L);

    /**
     * RefreshToken expiration time (default 15 days).
     */
    private Duration refreshTokenExpiresIn = Duration.ofDays(15L);

    /**
     * RefreshToken resource path.
     */
    private String refreshTokenPath;

    /**
     * Whether to enable {@link RefreshTokenApi} annotation-based configuration. When enabled, the {@code refreshTokenPath} configuration is ignored,
     * and the refresh token endpoint path is dynamically determined by the {@code @RefreshTokenApi} annotation's mapped endpoint path instead.
     */
    private Boolean enabledRefreshTokenApiAnnotation;

    /**
     * Accessible resource path.
     */
    private List<String> permitAllPaths = Arrays.asList(
            "/error",
            "/doc.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/*/api-docs/**",
            "/favicon.ico"
    );

    /**
     * Logout url.
     */
    private String logoutUrl;

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

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Duration getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Duration expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Duration getAllowedClockSkew() {
        return allowedClockSkew;
    }

    public void setAllowedClockSkew(Duration allowedClockSkew) {
        this.allowedClockSkew = allowedClockSkew;
    }

    public Duration getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public void setRefreshTokenExpiresIn(Duration refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public String getRefreshTokenPath() {
        return refreshTokenPath;
    }

    public void setRefreshTokenPath(String refreshTokenPath) {
        this.refreshTokenPath = refreshTokenPath;
    }

    public Boolean getEnabledRefreshTokenApiAnnotation() {
        return enabledRefreshTokenApiAnnotation;
    }

    public void setEnabledRefreshTokenApiAnnotation(Boolean enabledRefreshTokenApiAnnotation) {
        this.enabledRefreshTokenApiAnnotation = enabledRefreshTokenApiAnnotation;
    }

    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }
}
