package org.sunshine.core.oss.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Minio参数配置类
 *
 * @author Chill
 */
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;

    /**
     * Secret key是你账户的密码
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 自定义属性
     */
    private Map<String, Object> args;

    public enum ClientType {

        /**
         * 阿里云
         */
        ALIYUN,

        /**
         * Minio
         */
        MINIO
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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }
}
