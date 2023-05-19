package org.sunshine.security.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.sunshine.core.tool.util.SpringUtils;
import org.sunshine.security.core.enums.RoleEnum;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Security相关实用程序
 *
 * @author Teamo
 * @since 2023/3/14
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

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
     * 设置Authentication
     *
     * @param authentication Authentication
     */
    public static void setAuthentication(Authentication authentication) {
        getContext().setAuthentication(authentication);
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
     * 获取权限
     *
     * @return 权限Set
     */
    public static Set<String> getAuthoritySet() {
        Collection<? extends GrantedAuthority> userAuthorities = getAuthorities();
        if (userAuthorities == null) {
            return Collections.emptySet();
        }
        return AuthorityUtils.authorityListToSet(userAuthorities);
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

    /**
     * 是否是系统管理员
     *
     * @return ture or false
     */
    public static boolean isAdmin() {
        return hasRole(RoleEnum.RoleCode.ADMIN_ROLE_CODE);
    }

    /**
     * 是否有权限
     *
     * @param authority 权限不带ROLE_前缀
     * @return true or false
     */
    public final boolean hasAuthority(String authority) {
        return hasAnyAuthority(authority);
    }

    /**
     * 是否有权限
     *
     * @param authorities 权限不带ROLE_前缀
     * @return true or false
     */
    public final boolean hasAnyAuthority(String... authorities) {
        return hasAnyAuthorityName(null, authorities);
    }

    /**
     * 是否有角色
     *
     * @param role 角色带ROLE_前缀
     * @return true or false
     */
    public static boolean hasRole(String role) {
        return hasAnyRole(role);
    }

    /**
     * 是否有角色
     *
     * @param role 角色带ROLE_前缀
     * @return true or false
     */
    public static boolean hasAnyRole(String... role) {
        return hasAnyAuthorityName(RoleEnum.RoleCode.ROLE_PREFIX, role);
    }

    private static boolean hasAnyAuthorityName(String prefix, String... roles) {
        Set<String> roleSet = getAuthoritySet();
        return Arrays.stream(roles).anyMatch(role -> {
            String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
            return roleSet.contains(defaultedRole);
        });
    }

    private static String getRoleWithDefaultPrefix(String defaultRolePrefix, String role) {
        if (role == null) {
            return null;
        }
        if (defaultRolePrefix == null || defaultRolePrefix.length() == 0) {
            return role;
        }
        if (role.startsWith(defaultRolePrefix)) {
            return role;
        }
        return defaultRolePrefix + role;
    }
}
