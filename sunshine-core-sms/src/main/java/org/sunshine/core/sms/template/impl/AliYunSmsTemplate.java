package org.sunshine.core.sms.template.impl;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.sunshine.core.sms.model.SmsMessage;
import org.sunshine.core.sms.template.SmsTemplate;
import org.sunshine.core.tool.util.StringPool;

import java.util.Objects;

/**
 * @author Teamo
 * @since 2022/03/03
 */
public class AliYunSmsTemplate implements SmsTemplate {

    private final static Logger log = LoggerFactory.getLogger(AliYunSmsTemplate.class);

    private static final String OK = "OK";

    private static final int PHONE_NUMBERS_LIMIT = 1000;

    private final Client client;

    public AliYunSmsTemplate(Client client) {
        this.client = client;
    }

    @Override
    public boolean sendSms(SmsMessage smsMessage) {
        Assert.notNull(smsMessage, "SmsSender must not be null!");
        String[] phoneNumbers = smsMessage.getPhoneNumbers();
        Assert.notEmpty(phoneNumbers, "Phone numbers must not be empty!");
        if (phoneNumbers.length > PHONE_NUMBERS_LIMIT) {
            throw new IllegalArgumentException("The number of mobile phone numbers cannot be greater than 1000");
        }

        SmsMessage.AliYun aliYun = smsMessage.getAliYun();
        Assert.notNull(aliYun, "The AliYun SMS param must not be null!");

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(String.join(StringPool.COMMA, phoneNumbers))
                .setSignName(smsMessage.getSignName())
                .setTemplateCode(smsMessage.getTemplateCode())
                .setTemplateParam(aliYun.getTemplateParam())
                .setOutId(aliYun.getOutId());
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = client.sendSms(sendSmsRequest);
        } catch (Exception e) {
            log.error("Alibaba Cloud failed to send SMS exception:{}", e.getMessage(), e);
        }
        if (Objects.isNull(sendSmsResponse)) {
            return false;
        }
        SendSmsResponseBody body = sendSmsResponse.getBody();
        if (Objects.isNull(body)) {
            return false;
        }
        String message = body.getMessage();
        if (!OK.equals(message)) {
            log.warn("Alibaba Cloud fails to send SMS:{}", JSON.toJSONString(body));
        }
        return OK.equals(message);
    }
}
