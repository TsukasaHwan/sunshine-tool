package org.sunshine.oauth2.resource.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Teamo
 * @since 2023/6/8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ResourceServerConfiguration.class)
@Configuration
public @interface EnableResourceServer {
}
