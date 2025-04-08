package org.sunshine.oauth2.authorization.server;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.sunshine.core.tool.util.ClassUtils;
import org.sunshine.oauth2.authorization.server.properties.OAuth2AuthorizationServerProperties;
import org.sunshine.security.core.DefaultSecurityConfiguration;
import org.sunshine.security.core.handler.CommonAccessDeniedHandler;
import org.sunshine.security.core.handler.CommonAuthenticationEntryPoint;
import org.sunshine.security.core.support.PathPatternRequestMatcher;

import java.util.ArrayList;
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
@EnableConfigurationProperties(OAuth2AuthorizationServerProperties.class)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class OAuth2WebSecurityConfiguration {

    private final OAuth2AuthorizationServerProperties properties;

    private ApplicationContext context;

    public OAuth2WebSecurityConfiguration(OAuth2AuthorizationServerProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
            List<PathPatternRequestMatcher> matchers = extractPermitAllAnnotationPath();
            matchers.removeIf(matcher -> requestMatchers.stream().anyMatch(p -> p.equals(matcher)));
            if (!matchers.isEmpty()) {
                registry.requestMatchers(matchers.toArray(PathPatternRequestMatcher[]::new)).permitAll();
            }
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

    private List<PathPatternRequestMatcher> extractPermitAllAnnotationPath() {
        List<PathPatternRequestMatcher> matchers = new ArrayList<>(16);
        RequestMappingHandlerMapping mapping = this.context.getBean(RequestMappingHandlerMapping.class);
        mapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            if (requestMappingInfo == null ||
                ClassUtils.getAnnotation(handlerMethod, PermitAll.class) == null) {
                return;
            }

            // Handle different path matching strategies
            Set<String> patterns = new LinkedHashSet<>(16);
            PathPatternsRequestCondition pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
            if (pathPatternsCondition == null) {
                PatternsRequestCondition patternsRequestCondition = requestMappingInfo.getPatternsCondition();
                if (patternsRequestCondition == null) {
                    return;
                }
                patterns.addAll(patternsRequestCondition.getPatterns());
            } else {
                patterns.addAll(pathPatternsCondition.getPatternValues());
            }

            requestMappingInfo.getMethodsCondition().getMethods().forEach(requestMethod -> {
                HttpMethod httpMethod = HttpMethod.valueOf(requestMethod.name());

                patterns.forEach(pattern -> matchers.add(PathPatternRequestMatcher.withDefaults().matcher(httpMethod, pattern)));
            });
        });
        return matchers;
    }
}
