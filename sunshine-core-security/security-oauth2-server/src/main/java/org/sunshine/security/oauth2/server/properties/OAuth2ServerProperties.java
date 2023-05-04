package org.sunshine.security.oauth2.server.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@ConfigurationProperties("oauth2.server")
public class OAuth2ServerProperties {

    /**
     * Whether to enable OAuth2 Server.
     */
    private boolean enable = false;

    /**
     * Consent page uri
     */
    private String consentPageUri;

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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getConsentPageUri() {
        return consentPageUri;
    }

    public void setConsentPageUri(String consentPageUri) {
        this.consentPageUri = consentPageUri;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }
}
