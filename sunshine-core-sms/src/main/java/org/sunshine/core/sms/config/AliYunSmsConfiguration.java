package org.sunshine.core.sms.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunshine.core.sms.props.SmsProperties;
import org.sunshine.core.sms.template.impl.AliYunSmsTemplate;
import org.sunshine.core.tool.util.StringUtils;

/**
 * AliYunSms 短信配置类
 *
 * @author Teamo
 * @since 2022/03/03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Client.class)
@EnableConfigurationProperties(SmsProperties.class)
@ConditionalOnProperty(value = "sms.client-type", havingValue = "aliyun")
public class AliYunSmsConfiguration extends SmsConfiguration {

    protected AliYunSmsConfiguration(SmsProperties smsProperties) {
        super(smsProperties);
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client client() {
        SmsProperties smsProperties = getSmsProperties();

        Config config = new Config()
                .setAccessKeyId(smsProperties.getKeyId())
                .setAccessKeySecret(smsProperties.getKeySecret())
                .setEndpoint(smsProperties.getEndpoint());

        String regionId = smsProperties.getRegionId();
        if (StringUtils.isNotBlank(regionId)) {
            config.setRegionId(regionId);
        }

        try {
            return new Client(config);
        } catch (Exception e) {
            throw new BeanCreationException("failed to create Alibaba Cloud SMS client:" + e.getMessage());
        }
    }

    @Bean
    public AliYunSmsTemplate aliyunSmsTemplate(Client client) {
        return new AliYunSmsTemplate(client);
    }
}
