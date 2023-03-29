package org.sunshine.core.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Teamo
 * @since 2023/3/17
 */
public abstract class JwtUserDetails implements UserDetails {

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
