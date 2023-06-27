package org.sunshine.core.sms.template;

import org.springframework.mail.SimpleMailMessage;
import org.sunshine.core.sms.model.FileMailMessage;

/**
 * @author Teamo
 * @since 2023/5/5
 */
public interface MailTemplate {

    /**
     * 发送邮件
     *
     * @param simpleMailMessage {@link SimpleMailMessage}
     */
    void send(SimpleMailMessage... simpleMailMessage);

    /**
     * 发送带附件的邮件
     *
     * @param fileMailMessage {@link FileMailMessage}
     */
    void sendWithFile(FileMailMessage... fileMailMessage);
}
