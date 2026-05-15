package org.sunshine.core.security.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Teamo
 * @since 2023/6/5
 */
@Configuration(proxyBeanMethods = false)
public class SecurityComponentConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
