package org.sunshine.core.oss.config;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunshine.core.oss.MinioTemplate;
import org.sunshine.core.oss.props.OssProperties;
import org.sunshine.core.oss.rule.OssRule;

/**
 * @author Teamo
 * @since 2023/8/2
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "oss.client-type", havingValue = "minio")
public class MinioConfiguration {

    private final OssProperties ossProperties;

    public MinioConfiguration(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(ossProperties.getEndpoint())
                .credentials(ossProperties.getAccessKey(), ossProperties.getSecretKey())
                .build();
    }

    @Bean
    @ConditionalOnBean(MinioClient.class)
    @ConditionalOnMissingBean(MinioTemplate.class)
    public MinioTemplate minioTemplate(MinioClient minioClient, OssRule ossRule) {
        return new MinioTemplate(minioClient, ossRule, ossProperties);
    }
}
