package org.sunshine.security.oauth2.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@Getter
@Setter
@TableName(value = "sys_oauth2_auth")
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
}
