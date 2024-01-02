package org.sunshine.core.sms.template;

import org.sunshine.core.sms.model.SmsMessage;

/**
 * @author Teamo
 * @since 2023/5/5
 */
public interface SmsTemplate {

    /**
     * 发送短信
     *
     * @param smsMessage {@link SmsMessage}
     * @return 是否成功
     */
    boolean sendSms(SmsMessage smsMessage);
}
