package org.sunshine.security.oauth2.server;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.sunshine.security.core.DefaultSecurityConfiguration;
import org.sunshine.security.core.handler.CommonAccessDeniedHandler;
import org.sunshine.security.core.support.PermitAllAnnotationSupport;
import org.sunshine.security.oauth2.server.handler.OAuth2AuthenticationEntryPoint;
import org.sunshine.security.oauth2.server.properties.OAuth2ServerProperties;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Teamo
 * @since 2023/6/2
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@Import(DefaultSecurityConfiguration.class)
@EnableConfigurationProperties(OAuth2ServerProperties.class)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfiguration {

    private final OAuth2ServerProperties properties;

    public WebSecurityConfiguration(OAuth2ServerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   PermitAllAnnotationSupport permitAllAnnotationSupport) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        List<String> permitAllPaths = properties.getPermitAllPaths();
        if (!permitAllPaths.isEmpty()) {
            http.authorizeHttpRequests().antMatchers(permitAllPaths.toArray(new String[0])).permitAll();
        }
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry = http.authorizeHttpRequests();
        permitAllAnnotationSupport.getAntPatterns().forEach((httpMethod, antPatterns) -> {
            Set<String> antPatternsSet = new LinkedHashSet<>(antPatterns);
            antPatternsSet.removeIf(permitAllPaths::contains);
            if (!antPatternsSet.isEmpty()) {
                registry.antMatchers(httpMethod, antPatternsSet.toArray(new String[0])).permitAll();
            }
        });

        http
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling(exceptions -> {
                    exceptions.accessDeniedHandler(new CommonAccessDeniedHandler());
                    exceptions.authenticationEntryPoint(new OAuth2AuthenticationEntryPoint());
                })
                .csrf().disable()
                .headers().frameOptions().disable();
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
