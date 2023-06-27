package org.sunshine.core.sms.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Teamo
 * @since 2022/03/03
 */
@ConfigurationProperties("sms")
public class SmsProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 客户端类型
     */
    private ClientType clientType;

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

    /**
     * 地域参数
     */
    private String regionId;

    /**
     * 客户端类型
     */
    public enum ClientType {

        /**
         * 阿里云
         */
        ALIYUN,

        /**
         * 腾讯云
         */
        TENCENT
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
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

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
