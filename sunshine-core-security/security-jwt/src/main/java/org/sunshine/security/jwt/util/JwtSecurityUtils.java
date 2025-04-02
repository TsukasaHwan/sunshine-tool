package org.sunshine.security.jwt.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;
import org.sunshine.core.tool.util.WebUtils;
import org.sunshine.security.core.util.SecurityUtils;

import java.util.Map;

/**
 * @author Teamo
 * @since 2025/4/2
 */
public class JwtSecurityUtils extends SecurityUtils {

    /**
     * 获取当前请求的token
     *
     * @return token
     */
    public static String getCurrentToken() {
        return getToken(WebUtils.getRequest());
    }

    /**
     * 获取当前请求的Claims
     *
     * @return Claims
     */
    public static Claims getCurrentClaims() {
        return getClaims(WebUtils.getRequest());
    }

    /**
     * 获取当前请求的Claims
     *
     * @param claimTypeMap Claim类型
     * @return Claims
     */
    public static Claims getCurrentClaims(Map<String, Class<?>> claimTypeMap) {
        return getClaims(WebUtils.getRequest(), claimTypeMap);
    }

    /**
     * 获取当前请求的Claim值
     *
     * @param name      Claim名称
     * @param claimType Claim类型
     * @return Claim值
     */
    public static <T> T getCurrentClaimValue(String name, Class<T> claimType) {
        return getClaimValue(WebUtils.getRequest(), name, claimType);
    }

    /**
     * 获取指定请求的token
     *
     * @param request 请求
     * @return token
     */
    public static String getToken(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest must not be null");
        return JwtClaimsUtils.getToken(request);
    }

    /**
     * 获取指定请求的Claims
     *
     * @param request 请求
     * @return Claims
     */
    public static Claims getClaims(HttpServletRequest request) {
        return getClaims(request, null);
    }

    /**
     * 获取指定请求的Claims
     *
     * @param request      请求
     * @param claimTypeMap Claim类型
     * @return Claims
     */
    public static Claims getClaims(HttpServletRequest request, Map<String, Class<?>> claimTypeMap) {
        Assert.notNull(request, "HttpServletRequest must not be null");
        return JwtClaimsUtils.parseToken(getToken(request), claimTypeMap);
    }

    /**
     * 获取指定请求的Claim值
     *
     * @param request   请求
     * @param name      Claim名称
     * @param claimType Claim类型
     * @return Claim值
     */
    public static <T> T getClaimValue(HttpServletRequest request, String name, Class<T> claimType) {
        Assert.notNull(request, "HttpServletRequest must not be null");
        return JwtClaimsUtils.parseToken(getToken(request), Map.of(name, claimType)).get(name, claimType);
    }
}
