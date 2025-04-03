package org.sunshine.security.jwt.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Teamo
 * @since 2025/4/3
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshTokenApi {

    @AliasFor("claim")
    String value() default "refresh_token";

    @AliasFor("value")
    String claim() default "refresh_token";
}
