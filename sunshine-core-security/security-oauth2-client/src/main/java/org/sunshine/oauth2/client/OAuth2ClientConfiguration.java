package org.sunshine.oauth2.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.sunshine.core.tool.util.CollectionUtils;
import org.sunshine.oauth2.client.properties.OAuth2ClientProperties;
import org.sunshine.security.core.DefaultSecurityConfiguration;

import java.util.List;

/**
 * @author Teamo
 * @since 2023/6/9
 */
@EnableWebFluxSecurity
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@Import(DefaultSecurityConfiguration.class)
public class OAuth2ClientConfiguration {

    private final OAuth2ClientProperties properties;

    public OAuth2ClientConfiguration(OAuth2ClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        List<String> forbiddenPaths = properties.getForbiddenPaths();
        ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec = http.authorizeExchange();
        if (CollectionUtils.isNotEmpty(forbiddenPaths)) {
            authorizeExchangeSpec.pathMatchers(forbiddenPaths.toArray(new String[0])).denyAll();
        }
        authorizeExchangeSpec
                // 放行交由资源服务器进行认证鉴权
                .anyExchange().permitAll()
                .and()
                // 禁用csrf token安全校验
                .csrf().disable();
        return http.build();
    }
}
