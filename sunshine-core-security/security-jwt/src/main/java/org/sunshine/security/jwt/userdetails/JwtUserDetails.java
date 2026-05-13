package org.sunshine.security.jwt.userdetails;

import com.alibaba.fastjson2.annotation.JSONField;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Teamo
 * @since 2023/3/17
 */
public abstract class JwtUserDetails implements UserDetails {

    @Override
    @JSONField(serialize = false)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    public boolean isEnabled() {
        return true;
    }
}
