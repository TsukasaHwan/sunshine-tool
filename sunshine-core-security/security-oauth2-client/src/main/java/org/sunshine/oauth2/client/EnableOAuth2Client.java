package org.sunshine.oauth2.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Teamo
 * @since 2023/6/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(OAuth2ClientConfiguration.class)
@Configuration
public @interface EnableOAuth2Client {
}
