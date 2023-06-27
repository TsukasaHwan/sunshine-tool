package org.sunshine.core.sms;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.sunshine.core.sms.config.AliYunSmsConfiguration;
import org.sunshine.core.sms.config.TencentSmsConfiguration;

/**
 * @author Teamo
 * @since 2023/6/27
 */
@AutoConfiguration
@Import({AliYunSmsConfiguration.class, TencentSmsConfiguration.class})
@ConditionalOnProperty(value = "sms.enabled", havingValue = "true")
public class SmsAutoConfiguration {
}
