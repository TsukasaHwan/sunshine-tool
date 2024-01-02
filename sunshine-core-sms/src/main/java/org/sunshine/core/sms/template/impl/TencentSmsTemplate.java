package org.sunshine.core.sms.template.impl;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.sunshine.core.sms.model.SmsMessage;
import org.sunshine.core.sms.template.SmsTemplate;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Teamo
 * @since 2023/6/27
 */
public class TencentSmsTemplate implements SmsTemplate {

    private final static Logger log = LoggerFactory.getLogger(TencentSmsTemplate.class);

    private final static int PHONE_NUMBERS_LIMIT = 200;

    private static final String OK = "Ok";

    private final SmsClient smsClient;

    public TencentSmsTemplate(SmsClient smsClient) {
        this.smsClient = smsClient;
    }

    @Override
    public boolean sendSms(SmsMessage smsMessage) {
        Assert.notNull(smsMessage, "SmsSender must not be null!");
        String[] phoneNumbers = smsMessage.getPhoneNumbers();
        Assert.notEmpty(phoneNumbers, "Phone numbers must not be empty!");
        if (phoneNumbers.length > PHONE_NUMBERS_LIMIT) {
            throw new IllegalArgumentException("The number of mobile phone numbers cannot be greater than 200");
        }

        SmsMessage.Tencent tencent = smsMessage.getTencent();
        Assert.notNull(tencent, "The Tencent SMS param must not be null!");

        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppId(tencent.getSdkAppId());
        req.setSignName(smsMessage.getSignName());
        req.setTemplateId(smsMessage.getTemplateCode());
        req.setTemplateParamSet(tencent.getTemplateParam());
        req.setPhoneNumberSet(phoneNumbers);
        SendSmsResponse response = null;
        try {
            response = smsClient.SendSms(req);
        } catch (TencentCloudSDKException e) {
            log.error("Tencent Cloud failed to send SMS exception:{}", e.getMessage(), e);
        }
        if (Objects.isNull(response)) {
            return false;
        }
        String result = SendSmsResponse.toJsonString(response);
        SendStatus[] sendStatusSet = response.getSendStatusSet();
        boolean success = Arrays.stream(sendStatusSet).allMatch(sendStatus -> OK.equals(sendStatus.getCode()));
        if (!success) {
            log.info("Tencent Cloud fails to send SMS:{}", result);
        }
        return success;
    }
}
