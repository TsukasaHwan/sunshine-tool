package org.sunshine.core.sms;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.sunshine.core.sms.MailTemplate;
import org.sunshine.core.sms.SimpleMailTemplate;

/**
 * @author Teamo
 * @since 2023/5/5
 */
@AutoConfiguration
@ConditionalOnClass(JavaMailSender.class)
public class MailAutoConfiguration {

    @Bean
    @ConditionalOnBean(JavaMailSender.class)
    @ConditionalOnMissingBean(MailTemplate.class)
    public MailTemplate mailTemplate(JavaMailSender javaMailSender) {
        return new SimpleMailTemplate(javaMailSender);
    }
}
