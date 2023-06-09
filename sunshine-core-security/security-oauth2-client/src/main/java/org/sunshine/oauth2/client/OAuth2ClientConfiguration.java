package org.sunshine.oauth2.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.sunshine.core.tool.util.CollectionUtils;
import org.sunshine.oauth2.client.handler.WebFluxAuthenticationEntryPoint;
import org.sunshine.oauth2.client.properties.OAuth2ClientProperties;

import java.util.Arrays;
import java.util.List;

/**
 * @author Teamo
 * @since 2023/6/9
 */
@EnableWebFluxSecurity
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OAuth2ClientProperties.class)
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
                .exceptionHandling()
                .authenticationEntryPoint(new WebFluxAuthenticationEntryPoint())
                .and()
                // 禁用csrf token安全校验
                .csrf().disable();
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(CorsConfigurationSource.class)
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> allowMethod = Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.HEAD.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        );

        List<String> allowHeader = Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.ORIGIN,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                "X-Requested-With"
        );

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern(CorsConfiguration.ALL);
        corsConfiguration.setAllowedMethods(allowMethod);
        corsConfiguration.setAllowedHeaders(allowHeader);
        corsConfiguration.setAllowCredentials(Boolean.TRUE);
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
