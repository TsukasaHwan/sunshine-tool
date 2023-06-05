package org.sunshine.security.core.util;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

/**
 * @author Teamo
 * @since 2023/6/5
 */
public abstract class OAuth2SecurityUtils extends BaseSecurityUtils {

    /**
     * 获取主体
     *
     * @return Jwt
     */
    public static Jwt getPrincipal() {
        return (Jwt) getAuthentication().getPrincipal();
    }

    /**
     * 获取用户名
     *
     * @return String
     */
    public static String getUsername() {
        return getPrincipal().getSubject();
    }

    /**
     * 获取token属性
     *
     * @return Map<String, Object>
     */
    public static Map<String, Object> getClaims() {
        return getPrincipal().getClaims();
    }
}
