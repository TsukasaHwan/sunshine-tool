package org.sunshine.core.oss.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

/**
 * @author Teamo
 * @since 2023/8/2
 */
@AutoConfiguration(after = OssConfiguration.class)
@ConditionalOnProperty(value = "oss.enabled", havingValue = "true")
@Import({OssConfiguration.class, AliOssConfiguration.class, MinioConfiguration.class})
public class OssAutoConfiguration {
}
