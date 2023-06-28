package org.sunshine.core.sms.config;

import org.springframework.util.Assert;
import org.sunshine.core.sms.props.SmsProperties;

/**
 * @author Teamo
 * @since 2023/6/28
 */
public abstract class SmsConfiguration {

    private final SmsProperties smsProperties;

    protected SmsConfiguration(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    protected SmsProperties getSmsProperties() {
        String keyId = smsProperties.getKeyId();
        String keySecret = smsProperties.getKeySecret();
        Assert.notNull(keyId, "The SMS keyId must not be null!");
        Assert.notNull(keySecret, "The SMS keySecret must not be null!");

        SmsProperties.ClientType clientType = smsProperties.getClientType();
        Assert.notNull(clientType, "Please configure SMS client type.");

        if (SmsProperties.ClientType.ALIYUN.equals(clientType)) {
            String endpoint = smsProperties.getEndpoint();
            Assert.notNull(endpoint, "The AliYun SMS endpoint must not be null!");
        } else if (SmsProperties.ClientType.TENCENT.equals(clientType)) {
            String regionId = smsProperties.getRegionId();
            Assert.notNull(regionId, "The Tencent SMS regionId must not be null!");
        } else {
            throw new IllegalArgumentException("The SMS client type is not supported.");
        }

        return smsProperties;
    }
}
