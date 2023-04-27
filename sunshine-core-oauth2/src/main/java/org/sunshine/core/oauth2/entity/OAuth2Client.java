package org.sunshine.core.oauth2.entity;

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
@TableName(value = "sys_oauth2_client")
public class OAuth2Client {

    /**
     * 客户端ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
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
}
