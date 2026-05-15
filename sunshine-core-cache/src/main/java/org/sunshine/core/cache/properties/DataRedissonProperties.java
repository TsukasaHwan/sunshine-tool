package org.sunshine.core.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * @author Teamo
 * @since 2022/01/07
 */
@ConfigurationProperties("spring.data.redis.redisson")
public class DataRedissonProperties {

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

    /**
     * @deprecated 请使用 {@code enabled}
     */
    @Deprecated(forRemoval = true)
    @DeprecatedConfigurationProperty(replacement = "spring.data.redis.redisson.enabled")
    public Boolean getEnable() {
        return enabled;
    }

    @Deprecated(forRemoval = true)
    public void setEnable(Boolean enable) {
        this.enabled = enable;
    }
}