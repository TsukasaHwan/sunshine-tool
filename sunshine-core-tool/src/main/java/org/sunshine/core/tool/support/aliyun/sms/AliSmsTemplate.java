package org.sunshine.core.tool.support.aliyun.sms;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.sunshine.core.tool.support.aliyun.sms.model.AliSmsSender;

import java.util.Objects;

/**
 * @author Teamo
 * @since 2022/03/03
 */
public class AliSmsTemplate {

    private final static Logger log = LoggerFactory.getLogger(AliSmsTemplate.class);

    private final Client client;

    private static final String OK = "OK";

    public AliSmsTemplate(Client client) {
        this.client = client;
    }

    /**
     * 发送短信
     *
     * @param aliSmsSender {@link AliSmsSender}
     * @return 是否成功
     */
    public boolean sendSms(AliSmsSender aliSmsSender) {
        Assert.notNull(aliSmsSender, "AliSmsSender must not be null!");
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(aliSmsSender.getPhone())
                .setSignName(aliSmsSender.getSignName())
                .setTemplateCode(aliSmsSender.getTemplateCode())
                .setTemplateParam(aliSmsSender.getTemplateParam())
                .setOutId(aliSmsSender.getOutId());
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
            log.info("Alibaba Cloud fails to send SMS:{}", JSON.toJSONString(body));
        }
        return OK.equals(message);
    }
}
