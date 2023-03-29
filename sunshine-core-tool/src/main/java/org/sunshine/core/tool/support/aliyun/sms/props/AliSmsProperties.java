package org.sunshine.core.tool.support.aliyun.sms.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Teamo
 * @since 2022/03/03
 */
@ConfigurationProperties("aliyun.sms")
public class AliSmsProperties {
    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * key id
     */
    private String keyId;

    /**
     * key秘钥
     */
    private String keySecret;

    /**
     * 端点
     */
    private String endpoint;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public void setKeySecret(String keySecret) {
        this.keySecret = keySecret;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
