package org.sunshine.oauth2.authorization.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@TableName(value = "oauth2_authed_client")
public class OAuth2AuthedClient {

    /**
     * 客户端ID
     */
    @TableId(value = "id", type = IdType.NONE)
    private String id;

    /**
     * 客户端编号
     */
    private String clientId;

    /**
     * 客户端编号发布时间（创建时间）
     */
    private LocalDateTime clientIdIssuedAt;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 客户端密钥过期时间
     */
    private LocalDateTime clientSecretExpiresAt;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端身份验证方法（以,分割）
     * <p>
     * {@link org.springframework.security.oauth2.core.ClientAuthenticationMethod}
     */
    private String clientAuthenticationMethods;

    /**
     * 授权模式（以,分割）
     * <p>
     * {@link org.springframework.security.oauth2.core.AuthorizationGrantType}
     */
    private String authorizationGrantTypes;

    /**
     * 可重定向的URI地址（以,分割）
     */
    private String redirectUris;

    /**
     * 授权范围（以,分割）
     */
    private String scopes;

    /**
     * 客户端配置（JSON）
     * <p>
     * {@link org.springframework.security.oauth2.server.authorization.settings.ClientSettings}
     */
    private String clientSettings;

    /**
     * token配置（JSON）
     * <p>
     * {@link org.springframework.security.oauth2.server.authorization.settings.TokenSettings}
     */
    private String tokenSettings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    public void setClientIdIssuedAt(LocalDateTime clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public LocalDateTime getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public void setClientSecretExpiresAt(LocalDateTime clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAuthenticationMethods() {
        return clientAuthenticationMethods;
    }

    public void setClientAuthenticationMethods(String clientAuthenticationMethods) {
        this.clientAuthenticationMethods = clientAuthenticationMethods;
    }

    public String getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public void setAuthorizationGrantTypes(String authorizationGrantTypes) {
        this.authorizationGrantTypes = authorizationGrantTypes;
    }

    public String getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(String redirectUris) {
        this.redirectUris = redirectUris;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getClientSettings() {
        return clientSettings;
    }

    public void setClientSettings(String clientSettings) {
        this.clientSettings = clientSettings;
    }

    public String getTokenSettings() {
        return tokenSettings;
    }

    public void setTokenSettings(String tokenSettings) {
        this.tokenSettings = tokenSettings;
    }
}
