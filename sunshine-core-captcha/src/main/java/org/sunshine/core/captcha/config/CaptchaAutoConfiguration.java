package org.sunshine.core.captcha.config;

import com.anji.captcha.service.CaptchaCacheService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.sunshine.core.captcha.service.RedisCaptchaServiceImpl;

/**
 * @author Teamo
 * @since 2023/8/7
 */
@AutoConfiguration
public class CaptchaAutoConfiguration {

    @Bean
    public CaptchaCacheService captchaCacheService() {
        return new RedisCaptchaServiceImpl();
    }
}
