package org.sunshine.oauth2.authorization.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@TableName(value = "oauth2_auth_consent")
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

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }
}
