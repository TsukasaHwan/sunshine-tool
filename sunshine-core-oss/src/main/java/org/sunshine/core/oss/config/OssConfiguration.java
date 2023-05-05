package org.sunshine.core.oss.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunshine.core.oss.rule.CommonRule;
import org.sunshine.core.oss.rule.OssRule;

/**
 * @author Teamo
 * @since 2023/5/5
 */
@Configuration(proxyBeanMethods = false)
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    public OssRule ossRule() {
        return new CommonRule();
    }
}
