package org.sunshine.security.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.sunshine.core.tool.util.SpringUtils;

import java.util.Collection;

/**
 * Security相关实用程序
 *
 * @author Teamo
 * @since 2023/3/14
 */
public class SecurityUtils {

    /**
     * 获取上下文
     *
     * @return SecurityContext
     */
    public static SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * 清除上下文
     */
    public static void clearContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 获取Authentication
     *
     * @return Authentication
     */
    public static Authentication getAuthentication() {
        return getContext().getAuthentication();
    }

    /**
     * 获取权限
     *
     * @return 权限集合
     */
    public static Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthentication().getAuthorities();
    }

    /**
     * 设置Authentication
     *
     * @param authentication Authentication
     */
    public static void setAuthentication(Authentication authentication) {
        getContext().setAuthentication(authentication);
    }

    /**
     * 获取主体
     *
     * @return UserDetails
     */
    public static UserDetails getPrincipal() {
        return (UserDetails) getAuthentication().getPrincipal();
    }

    /**
     * 获取主体
     *
     * @return UserDetails
     */
    public static UserDetails getPrincipal(Authentication authentication) {
        return (UserDetails) authentication.getPrincipal();
    }

    /**
     * 匹配密码是否相同
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 编码密码
     * @return boolean
     */
    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        PasswordEncoder encoder = SpringUtils.getBean(PasswordEncoder.class);
        return encoder.matches(rawPassword, encodedPassword);
    }
}
