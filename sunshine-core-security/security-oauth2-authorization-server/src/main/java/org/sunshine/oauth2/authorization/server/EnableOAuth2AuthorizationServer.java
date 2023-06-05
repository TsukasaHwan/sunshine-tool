package org.sunshine.oauth2.authorization.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Teamo
 * @since 2023/6/2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({OAuth2AuthorizationServerConfiguration.class, OAuth2WebSecurityConfiguration.class})
@Configuration
public @interface EnableOAuth2AuthorizationServer {
}
