package org.sunshine.core.sms;

import org.sunshine.core.sms.model.SmsSender;

/**
 * @author Teamo
 * @since 2023/5/5
 */
public interface SmsTemplate {

    /**
     * 发生短信
     *
     * @param smsSender {@link SmsSender}
     * @return 是否成功
     */
    boolean sendSms(SmsSender smsSender);
}
