package org.sunshine.core.oauth2.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@ConfigurationProperties("spring.oauth2.server")
public class OAuth2ServerProperties {

    private boolean enable = false;

    /**
     * RSA public key
     */
    private RSAPublicKey publicKey;

    /**
     * RSA Private Key
     */
    private RSAPrivateKey privateKey;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
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
