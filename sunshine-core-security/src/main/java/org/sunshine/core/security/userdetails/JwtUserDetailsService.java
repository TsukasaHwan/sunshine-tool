package org.sunshine.core.security.userdetails;

import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Teamo
 * @since 2023/3/16
 */
public interface JwtUserDetailsService extends UserDetailsService {

    /**
     * 注销成功调用逻辑,username有可能为null,当token错误或过期时username为null
     *
     * @param username 用户名(有可能为null)
     */
    default void onLogoutSuccess(@Nullable String username) {
    }
}
