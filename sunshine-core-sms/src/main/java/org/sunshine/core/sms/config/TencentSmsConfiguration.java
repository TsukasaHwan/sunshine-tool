package org.sunshine.core.sms.config;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunshine.core.sms.props.SmsProperties;
import org.sunshine.core.sms.template.impl.TencentSmsTemplate;
import org.sunshine.core.tool.util.StringUtils;

/**
 * TencentSms 短信配置类
 *
 * @author Teamo
 * @since 2023/6/27
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SmsClient.class)
@EnableConfigurationProperties(SmsProperties.class)
@ConditionalOnProperty(value = "sms.client-type", havingValue = "tencent")
public class TencentSmsConfiguration extends SmsConfiguration {

    protected TencentSmsConfiguration(SmsProperties smsProperties) {
        super(smsProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SmsClient.class)
    public SmsClient smsClient() {
        SmsProperties smsProperties = getSmsProperties();

        Credential cred = new Credential(smsProperties.getKeyId(), smsProperties.getKeySecret());

        String regionId = smsProperties.getRegionId();
        String endpoint = smsProperties.getEndpoint();
        if (StringUtils.isNotBlank(endpoint)) {
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(endpoint);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            return new SmsClient(cred, regionId, clientProfile);
        }

        return new SmsClient(cred, regionId);
    }

    @Bean
    public TencentSmsTemplate tencentSmsTemplate(SmsClient smsClient) {
        return new TencentSmsTemplate(smsClient);
    }
}
