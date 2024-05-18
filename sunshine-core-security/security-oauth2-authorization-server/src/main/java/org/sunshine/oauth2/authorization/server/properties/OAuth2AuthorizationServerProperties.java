package org.sunshine.oauth2.authorization.server.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@ConfigurationProperties("oauth2.authorization.server")
public class OAuth2AuthorizationServerProperties {

    /**
     * Consent page uri
     */
    private String consentPageUri;

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
     * secret
     */
    private Secret secret = new Secret();

    public static class Secret {

        /**
         * kid
         */
        private String keyId;

        /**
         * RSA public key
         */
        private RSAPublicKey publicKey;

        /**
         * RSA Private Key
         */
        private RSAPrivateKey privateKey;

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

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

    public String getConsentPageUri() {
        return consentPageUri;
    }

    public void setConsentPageUri(String consentPageUri) {
        this.consentPageUri = consentPageUri;
    }

    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }
}
