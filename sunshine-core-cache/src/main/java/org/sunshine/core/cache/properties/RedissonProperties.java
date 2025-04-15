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
    private Boolean enable;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
