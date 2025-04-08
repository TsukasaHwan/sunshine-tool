package org.sunshine.oauth2.resource.server;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.sunshine.core.tool.util.ClassUtils;
import org.sunshine.oauth2.resource.server.properties.OAuth2ResourceServerProperties;
import org.sunshine.security.core.SecurityComponentConfiguration;
import org.sunshine.security.core.access.CommonAccessDeniedHandler;
import org.sunshine.security.core.authentication.CommonAuthenticationEntryPoint;
import org.sunshine.security.core.enums.RoleEnum;
import org.sunshine.security.core.oauth2.TokenConstant;
import org.sunshine.security.core.support.PathPatternRequestMatcher;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Teamo
 * @since 2023/6/5
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableConfigurationProperties(OAuth2ResourceServerProperties.class)
@Import(SecurityComponentConfiguration.class)
public class ResourceServerConfiguration {

    private final OAuth2ResourceServerProperties properties;

    private ApplicationContext context;

    public ResourceServerConfiguration(OAuth2ResourceServerProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        List<PathPatternRequestMatcher> requestMatchers =
                this.properties.getPermitAllPaths().stream()
                        .distinct()
                        .map(path -> PathPatternRequestMatcher.withDefaults().matcher(path))
                        .toList();
        http.authorizeHttpRequests(authorize -> {
            if (!requestMatchers.isEmpty()) {
                authorize.requestMatchers(requestMatchers.toArray(PathPatternRequestMatcher[]::new)).permitAll();
            }
            List<PathPatternRequestMatcher> matchers = extractPermitAllAnnotationPath();
            matchers.removeIf(matcher -> requestMatchers.stream().anyMatch(p -> p.equals(matcher)));
            if (!matchers.isEmpty()) {
                authorize.requestMatchers(matchers.toArray(PathPatternRequestMatcher[]::new)).permitAll();
            }
            authorize.anyRequest().authenticated();
        });

        http.csrf(AbstractHttpConfigurer::disable);

        http.oauth2ResourceServer(oauth2ResourceServer -> {
            oauth2ResourceServer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter));
            oauth2ResourceServer.authenticationEntryPoint(new CommonAuthenticationEntryPoint()).accessDeniedHandler(new CommonAccessDeniedHandler());
        });

        return http.build();
    }

    /**
     * 自定义JWT Converter
     *
     * @return {@link JwtAuthenticationProvider#setJwtAuthenticationConverter(Converter)}
     */
    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationConverter.class)
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(TokenConstant.AUTHORITIES);
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(RoleEnum.RoleCode.ROLE_PREFIX);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
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
