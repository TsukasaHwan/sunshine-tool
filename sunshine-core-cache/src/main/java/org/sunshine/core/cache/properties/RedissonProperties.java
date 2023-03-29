package org.sunshine.core.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Teamo
 * @since 2022/01/07
 */
@ConfigurationProperties("spring.redis.redisson")
public class RedissonProperties {
    /**
     * whether to enable
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
