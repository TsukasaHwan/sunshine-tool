package org.sunshine.security.jwt;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import jakarta.annotation.security.PermitAll;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.sunshine.security.core.access.CommonAccessDeniedHandler;
import org.sunshine.security.core.authentication.NoOpAuthenticationSuccessHandler;
import org.sunshine.security.jwt.authentication.JwtAuthenticationEntryPoint;
import org.sunshine.security.jwt.authentication.JwtLogoutSuccessHandler;
import org.sunshine.security.jwt.authenticator.AccessTokenAuthenticator;
import org.sunshine.security.jwt.authenticator.RefreshTokenAuthenticator;
import org.sunshine.security.jwt.authenticator.TokenAuthenticatorRegistry;
import org.sunshine.security.jwt.util.JwtUtils;

import java.util.Optional;

/**
 * @author Teamo
 * @since 2025/4/3
 */
public class JwtSecurityComponent {

    @Bean
    @Primary
    @ConditionalOnClass(OperationCustomizer.class)
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            boolean empty = Optional.ofNullable(handlerMethod.getMethodAnnotation(PermitAll.class)).or(() -> {
                Class<?> beanType = handlerMethod.getBeanType();
                return Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(beanType, PermitAll.class));
            }).isEmpty();
            if (empty) {
                String header = JwtUtils.getTokenHeader();
                String tokenPrefix = JwtUtils.getTokenPrefix();
                @SuppressWarnings("rawtypes")
                Schema stringSchema = new StringSchema()._default(tokenPrefix).name(header).description("请求接口凭证");
                Parameter headerParameter = new HeaderParameter().name(header).description("请求接口凭证").schema(stringSchema);
                operation.addParametersItem(headerParameter);
            }
            return operation;
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler noOpAuthenticationSuccessHandler() {
        return new NoOpAuthenticationSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler commnonAccessDeniedHandler() {
        return new CommonAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenAuthenticator accessTokenAuthenticator(UserDetailsService userDetailsService) {
        return new AccessTokenAuthenticator(userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RefreshTokenAuthenticator refreshTokenAuthenticator(UserDetailsService userDetailsService) {
        return new RefreshTokenAuthenticator(userDetailsService);
    }

    @Bean
    public TokenAuthenticatorRegistry tokenAuthenticatorRegistry() {
        return new TokenAuthenticatorRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogoutSuccessHandler jwtLogoutSuccessHandler() {
        return new JwtLogoutSuccessHandler();
    }

}
