package org.sunshine.oauth2.resource.server;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.sunshine.oauth2.resource.server.properties.OAuth2ResourceServerProperties;
import org.sunshine.security.core.SecurityComponentConfiguration;
import org.sunshine.security.core.handler.CommonAccessDeniedHandler;
import org.sunshine.security.core.handler.CommonAuthenticationEntryPoint;
import org.sunshine.security.core.oauth2.TokenConstant;
import org.sunshine.security.core.support.PermitAllAnnotationSupport;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Teamo
 * @since 2023/6/5
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableConfigurationProperties(OAuth2ResourceServerProperties.class)
@Import(SecurityComponentConfiguration.class)
public class ResourceServerConfiguration {

    private final OAuth2ResourceServerProperties properties;

    public ResourceServerConfiguration(OAuth2ResourceServerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   PermitAllAnnotationSupport permitAllAnnotationSupport,
                                                   Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry = http.authorizeHttpRequests();
        List<String> permitAllPaths = properties.getPermitAllPaths();
        if (!permitAllPaths.isEmpty()) {
            registry.antMatchers(permitAllPaths.toArray(new String[0])).permitAll();
        }
        permitAllAnnotationSupport.getAntPatterns().forEach((httpMethod, antPatterns) -> {
            Set<String> antPatternsSet = new LinkedHashSet<>(antPatterns);
            antPatternsSet.removeIf(permitAllPaths::contains);
            if (!antPatternsSet.isEmpty()) {
                registry.antMatchers(httpMethod, antPatternsSet.toArray(new String[0])).permitAll();
            }
        });

        registry.anyRequest().authenticated();

        http.csrf().disable();

        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                .and()
                .authenticationEntryPoint(new CommonAuthenticationEntryPoint())
                .accessDeniedHandler(new CommonAccessDeniedHandler());

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
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
