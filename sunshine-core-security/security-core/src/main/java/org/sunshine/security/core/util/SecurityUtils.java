package org.sunshine.security.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Security相关实用程序
 *
 * @author Teamo
 * @since 2023/3/14
 */
public abstract class SecurityUtils extends BaseSecurityUtils {

    /**
     * 获取主体
     *
     * @return UserDetails
     */
    public static UserDetails getPrincipal() {
        return getAuthentication().getPrincipal() instanceof UserDetails userDetails ? userDetails : null;
    }

    /**
     * 获取指定类型主体
     *
     * @param clazz class
     * @param <T>   泛型
     * @return 实际类型
     */
    public static <T extends UserDetails> T getPrincipal(Class<T> clazz) {
        return clazz.cast(getPrincipal());
    }

    /**
     * 获取指定类型主体
     *
     * @param authentication Authentication
     * @param clazz          class
     * @param <T>            泛型
     * @return 实际类型
     */
    public static <T extends UserDetails> T getPrincipal(Authentication authentication, Class<T> clazz) {
        return clazz.cast(getPrincipal(authentication));
    }

    /**
     * 获取主体
     *
     * @param authentication Authentication
     * @return UserDetails
     */
    public static UserDetails getPrincipal(Authentication authentication) {
        return authentication.getPrincipal() instanceof UserDetails userDetails ? userDetails : null;
    }

}
