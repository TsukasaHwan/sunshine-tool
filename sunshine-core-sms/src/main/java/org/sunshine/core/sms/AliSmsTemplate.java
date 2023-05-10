package org.sunshine.core.sms;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.sunshine.core.sms.model.SmsSender;
import org.sunshine.core.tool.util.StringPool;

import java.util.Objects;

/**
 * @author Teamo
 * @since 2022/03/03
 */
public class AliSmsTemplate implements SmsTemplate {

    private final static Logger log = LoggerFactory.getLogger(AliSmsTemplate.class);

    private static final String OK = "OK";

    private static final int PHONE_NUMBERS_LIMIT = 1000;

    private final Client client;

    public AliSmsTemplate(Client client) {
        this.client = client;
    }

    @Override
    public boolean sendSms(SmsSender smsSender) {
        Assert.notNull(smsSender, "SmsSender must not be null!");
        String[] phoneNumbers = smsSender.getPhoneNumbers();
        Assert.notEmpty(phoneNumbers, "Phone numbers must not be empty!");
        if (phoneNumbers.length > PHONE_NUMBERS_LIMIT) {
            throw new IllegalArgumentException("The number of mobile phone numbers cannot be greater than 1000");
        }

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(String.join(StringPool.COMMA, phoneNumbers))
                .setSignName(smsSender.getSignName())
                .setTemplateCode(smsSender.getTemplateCode())
                .setTemplateParam(smsSender.getTemplateParam())
                .setOutId(smsSender.getOutId());
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
