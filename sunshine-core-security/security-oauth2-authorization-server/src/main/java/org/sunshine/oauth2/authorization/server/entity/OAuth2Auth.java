package org.sunshine.oauth2.authorization.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@TableName(value = "oauth2_auth")
public class OAuth2Auth {

    /**
     * 授权ID
     */
    @TableId(value = "id", type = IdType.NONE)
    private String id;

    /**
     * 客户端ID
     */
    private String registeredClientId;

    /**
     * 授权名称（用户或者客户端编号）
     */
    private String principalName;

    /**
     * 授权模式
     * <p>
     * {@link org.springframework.security.oauth2.core.AuthorizationGrantType}
     */
    private String authorizationGrantType;

    /**
     * 授权范围（以,分割）
     */
    private String authorizedScopes;

    /**
     * 属性（JSON）
     */
    private String attributes;

    /**
     * 状态
     */
    private String state;

    /**
     * 授权码
     */
    private String authorizationCodeValue;

    /**
     * 授权码发布时间
     */
    private LocalDateTime authorizationCodeIssuedAt;

    /**
     * 授权码过期时间
     */
    private LocalDateTime authorizationCodeExpiresAt;

    /**
     * 授权码元数据（JSON）
     */
    private String authorizationCodeMetadata;

    /**
     * 访问令牌值
     */
    private String accessTokenValue;

    /**
     * 访问令牌发布时间
     */
    private LocalDateTime accessTokenIssuedAt;

    /**
     * 访问令牌过期时间
     */
    private LocalDateTime accessTokenExpiresAt;

    /**
     * 访问令牌元数据（JSON）
     */
    private String accessTokenMetadata;

    /**
     * 访问令牌类型（PS:Bearer）
     * <p>
     * {@link org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType}
     */
    private String accessTokenType;

    /**
     * 访问令牌范围
     */
    private String accessTokenScopes;

    /**
     * oidcId令牌值
     */
    private String oidcIdTokenValue;

    /**
     * oidcId令牌发布时间
     */
    private LocalDateTime oidcIdTokenIssuedAt;

    /**
     * oidcId令牌过期时间
     */
    private LocalDateTime oidcIdTokenExpiresAt;

    /**
     * oidcId令牌元数据（JSON）
     */
    private String oidcIdTokenMetadata;

    /**
     * 刷新令牌值
     */
    private String refreshTokenValue;

    /**
     * 刷新令牌发布时间
     */
    private LocalDateTime refreshTokenIssuedAt;

    /**
     * 刷新令牌过期时间
     */
    private LocalDateTime refreshTokenExpiresAt;

    /**
     * 刷新令牌元数据（JSON）
     */
    private String refreshTokenMetadata;

    /**
     * 用户码值
     */
    private String userCodeValue;

    /**
     * 用户码发布时间
     */
    private LocalDateTime userCodeIssuedAt;

    /**
     * 用户码过期时间
     */
    private LocalDateTime userCodeExpiresAt;

    /**
     * 用户码元数据（JSON）
     */
    private String userCodeMetadata;

    /**
     * 设备码值
     */
    private String deviceCodeValue;

    /**
     * 设备码发布时间
     */
    private LocalDateTime deviceCodeIssuedAt;

    /**
     * 设备码过期时间
     */
    private LocalDateTime deviceCodeExpiresAt;

    /**
     * 设备码元数据（JSON）
     */
    private String deviceCodeMetadata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId) {
        this.registeredClientId = registeredClientId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }

    public String getAuthorizedScopes() {
        return authorizedScopes;
    }

    public void setAuthorizedScopes(String authorizedScopes) {
        this.authorizedScopes = authorizedScopes;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthorizationCodeValue() {
        return authorizationCodeValue;
    }

    public void setAuthorizationCodeValue(String authorizationCodeValue) {
        this.authorizationCodeValue = authorizationCodeValue;
    }

    public LocalDateTime getAuthorizationCodeIssuedAt() {
        return authorizationCodeIssuedAt;
    }

    public void setAuthorizationCodeIssuedAt(LocalDateTime authorizationCodeIssuedAt) {
        this.authorizationCodeIssuedAt = authorizationCodeIssuedAt;
    }

    public LocalDateTime getAuthorizationCodeExpiresAt() {
        return authorizationCodeExpiresAt;
    }

    public void setAuthorizationCodeExpiresAt(LocalDateTime authorizationCodeExpiresAt) {
        this.authorizationCodeExpiresAt = authorizationCodeExpiresAt;
    }

    public String getAuthorizationCodeMetadata() {
        return authorizationCodeMetadata;
    }

    public void setAuthorizationCodeMetadata(String authorizationCodeMetadata) {
        this.authorizationCodeMetadata = authorizationCodeMetadata;
    }

    public String getAccessTokenValue() {
        return accessTokenValue;
    }

    public void setAccessTokenValue(String accessTokenValue) {
        this.accessTokenValue = accessTokenValue;
    }

    public LocalDateTime getAccessTokenIssuedAt() {
        return accessTokenIssuedAt;
    }

    public void setAccessTokenIssuedAt(LocalDateTime accessTokenIssuedAt) {
        this.accessTokenIssuedAt = accessTokenIssuedAt;
    }

    public LocalDateTime getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(LocalDateTime accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public String getAccessTokenMetadata() {
        return accessTokenMetadata;
    }

    public void setAccessTokenMetadata(String accessTokenMetadata) {
        this.accessTokenMetadata = accessTokenMetadata;
    }

    public String getAccessTokenType() {
        return accessTokenType;
    }

    public void setAccessTokenType(String accessTokenType) {
        this.accessTokenType = accessTokenType;
    }

    public String getAccessTokenScopes() {
        return accessTokenScopes;
    }

    public void setAccessTokenScopes(String accessTokenScopes) {
        this.accessTokenScopes = accessTokenScopes;
    }

    public String getOidcIdTokenValue() {
        return oidcIdTokenValue;
    }

    public void setOidcIdTokenValue(String oidcIdTokenValue) {
        this.oidcIdTokenValue = oidcIdTokenValue;
    }

    public LocalDateTime getOidcIdTokenIssuedAt() {
        return oidcIdTokenIssuedAt;
    }

    public void setOidcIdTokenIssuedAt(LocalDateTime oidcIdTokenIssuedAt) {
        this.oidcIdTokenIssuedAt = oidcIdTokenIssuedAt;
    }

    public LocalDateTime getOidcIdTokenExpiresAt() {
        return oidcIdTokenExpiresAt;
    }

    public void setOidcIdTokenExpiresAt(LocalDateTime oidcIdTokenExpiresAt) {
        this.oidcIdTokenExpiresAt = oidcIdTokenExpiresAt;
    }

    public String getOidcIdTokenMetadata() {
        return oidcIdTokenMetadata;
    }

    public void setOidcIdTokenMetadata(String oidcIdTokenMetadata) {
        this.oidcIdTokenMetadata = oidcIdTokenMetadata;
    }

    public String getRefreshTokenValue() {
        return refreshTokenValue;
    }

    public void setRefreshTokenValue(String refreshTokenValue) {
        this.refreshTokenValue = refreshTokenValue;
    }

    public LocalDateTime getRefreshTokenIssuedAt() {
        return refreshTokenIssuedAt;
    }

    public void setRefreshTokenIssuedAt(LocalDateTime refreshTokenIssuedAt) {
        this.refreshTokenIssuedAt = refreshTokenIssuedAt;
    }

    public LocalDateTime getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(LocalDateTime refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getRefreshTokenMetadata() {
        return refreshTokenMetadata;
    }

    public void setRefreshTokenMetadata(String refreshTokenMetadata) {
        this.refreshTokenMetadata = refreshTokenMetadata;
    }

    public String getUserCodeValue() {
        return userCodeValue;
    }

    public void setUserCodeValue(String userCodeValue) {
        this.userCodeValue = userCodeValue;
    }

    public LocalDateTime getUserCodeIssuedAt() {
        return userCodeIssuedAt;
    }

    public void setUserCodeIssuedAt(LocalDateTime userCodeIssuedAt) {
        this.userCodeIssuedAt = userCodeIssuedAt;
    }

    public LocalDateTime getUserCodeExpiresAt() {
        return userCodeExpiresAt;
    }

    public void setUserCodeExpiresAt(LocalDateTime userCodeExpiresAt) {
        this.userCodeExpiresAt = userCodeExpiresAt;
    }

    public String getUserCodeMetadata() {
        return userCodeMetadata;
    }

    public void setUserCodeMetadata(String userCodeMetadata) {
        this.userCodeMetadata = userCodeMetadata;
    }

    public String getDeviceCodeValue() {
        return deviceCodeValue;
    }

    public void setDeviceCodeValue(String deviceCodeValue) {
        this.deviceCodeValue = deviceCodeValue;
    }

    public LocalDateTime getDeviceCodeIssuedAt() {
        return deviceCodeIssuedAt;
    }

    public void setDeviceCodeIssuedAt(LocalDateTime deviceCodeIssuedAt) {
        this.deviceCodeIssuedAt = deviceCodeIssuedAt;
    }

    public LocalDateTime getDeviceCodeExpiresAt() {
        return deviceCodeExpiresAt;
    }

    public void setDeviceCodeExpiresAt(LocalDateTime deviceCodeExpiresAt) {
        this.deviceCodeExpiresAt = deviceCodeExpiresAt;
    }

    public String getDeviceCodeMetadata() {
        return deviceCodeMetadata;
    }

    public void setDeviceCodeMetadata(String deviceCodeMetadata) {
        this.deviceCodeMetadata = deviceCodeMetadata;
    }
}
