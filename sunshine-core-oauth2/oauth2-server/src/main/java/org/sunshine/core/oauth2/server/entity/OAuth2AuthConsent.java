package org.sunshine.core.oauth2.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@Getter
@Setter
@TableName(value = "sys_oauth2_auth_consent")
public class OAuth2AuthConsent {

    /**
     * 客户端ID
     */
    @TableId(value = "registered_client_id", type = IdType.NONE)
    private String registeredClientId;

    /**
     * 授权名称（用户或者客户端编号）
     */
    private String principalName;

    /**
     * 权限
     */
    private String authorities;
}
