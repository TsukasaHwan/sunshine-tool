package org.sunshine.core.tool.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.sunshine.core.tool.support.aliyun.sms.AliSmsTemplate;
import org.sunshine.core.tool.support.aliyun.sms.props.AliSmsProperties;

/**
 * AliSms 短信配置类
 *
 * @author Teamo
 * @since 2022/03/03
 */
@AutoConfiguration
@EnableConfigurationProperties(AliSmsProperties.class)
@ConditionalOnProperty(value = "aliyun.sms.enabled", havingValue = "true")
public class AliSmsConfiguration {

    private final AliSmsProperties aliSmsProperties;

    public AliSmsConfiguration(AliSmsProperties aliSmsProperties) {
        this.aliSmsProperties = aliSmsProperties;
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client client() {
        Config config = new Config()
                .setAccessKeyId(aliSmsProperties.getKeyId())
                .setAccessKeySecret(aliSmsProperties.getKeySecret())
                .setEndpoint(aliSmsProperties.getEndpoint());
        try {
            return new Client(config);
        } catch (Exception e) {
            throw new BeanCreationException("failed to create Alibaba Cloud SMS client:" + e.getMessage());
        }
    }

    @Bean
    @ConditionalOnMissingBean(AliSmsTemplate.class)
    public AliSmsTemplate aliSmsTemplate(Client client) {
        return new AliSmsTemplate(client);
    }
}
