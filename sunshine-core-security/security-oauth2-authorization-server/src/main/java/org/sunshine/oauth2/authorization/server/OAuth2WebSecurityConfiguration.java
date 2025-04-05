package org.sunshine.oauth2.authorization.server;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.sunshine.oauth2.authorization.server.properties.OAuth2AuthorizationServerProperties;
import org.sunshine.security.core.DefaultSecurityConfiguration;
import org.sunshine.security.core.handler.CommonAccessDeniedHandler;
import org.sunshine.security.core.handler.CommonAuthenticationEntryPoint;
import org.sunshine.security.core.support.PathPatternRequestMatcher;
import org.sunshine.security.core.support.PermitAllAnnotationExtractor;
import org.sunshine.security.core.support.SecurityAnnotationPathMatcherExtractor;

import java.util.List;

/**
 * @author Teamo
 * @since 2023/6/2
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@Import(DefaultSecurityConfiguration.class)
@EnableConfigurationProperties(OAuth2AuthorizationServerProperties.class)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class OAuth2WebSecurityConfiguration {

    private final OAuth2AuthorizationServerProperties properties;

    public OAuth2WebSecurityConfiguration(OAuth2AuthorizationServerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   List<SecurityAnnotationPathMatcherExtractor> securityAnnotationPathMatcherExtractors) throws Exception {
        http.sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        List<PathPatternRequestMatcher> requestMatchers =
                this.properties.getPermitAllPaths().stream()
                        .distinct()
                        .map(path -> PathPatternRequestMatcher.withDefaults().matcher(path))
                        .toList();
        http.authorizeHttpRequests(registry -> {
            if (!requestMatchers.isEmpty()) {
                registry.requestMatchers(requestMatchers.toArray(PathPatternRequestMatcher[]::new)).permitAll();
            }
            securityAnnotationPathMatcherExtractors.forEach(extractor -> {
                if (extractor instanceof PermitAllAnnotationExtractor permitAllAnnotationExtractor) {
                    List<PathPatternRequestMatcher> matchers = permitAllAnnotationExtractor.getPathPatternRequestMatchers();
                    matchers.removeIf(matcher -> requestMatchers.stream().anyMatch(p -> p.equals(matcher)));
                    if (!matchers.isEmpty()) {
                        registry.requestMatchers(matchers.toArray(PathPatternRequestMatcher[]::new)).permitAll();
                    }
                }
            });
            registry.anyRequest().authenticated();
        });

        // @formatter:off
        http
            .exceptionHandling(exceptions -> {
                exceptions.accessDeniedHandler(new CommonAccessDeniedHandler());
                exceptions.authenticationEntryPoint(new CommonAuthenticationEntryPoint());
            })
            .csrf(AbstractHttpConfigurer::disable)
            .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        // @formatter:on
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(DaoAuthenticationProvider.class)
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        // 是否隐藏用户不存在异常，默认:true-隐藏；false-抛出异常；
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }
}
