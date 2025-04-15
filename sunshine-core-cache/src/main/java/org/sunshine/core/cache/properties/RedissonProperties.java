package org.sunshine.core.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Teamo
 * @since 2022/01/07
 */
@ConfigurationProperties("spring.data.redis.redisson")
public class RedissonProperties {

    /**
     * Whether to enable
     */
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
