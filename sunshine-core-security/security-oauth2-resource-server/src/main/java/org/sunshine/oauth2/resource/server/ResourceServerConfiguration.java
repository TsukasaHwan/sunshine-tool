package org.sunshine.oauth2.resource.server;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.sunshine.oauth2.resource.server.properties.OAuth2ResourceServerProperties;
import org.sunshine.security.core.SecurityComponentConfiguration;
import org.sunshine.security.core.enums.RoleEnum;
import org.sunshine.security.core.handler.CommonAccessDeniedHandler;
import org.sunshine.security.core.handler.CommonAuthenticationEntryPoint;
import org.sunshine.security.core.oauth2.TokenConstant;
import org.sunshine.security.core.support.AbstractSecurityAnnotationSupport;

import java.util.List;

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

    public ResourceServerConfiguration(OAuth2ResourceServerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   List<AbstractSecurityAnnotationSupport> securityAnnotationSupportList,
                                                   Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        List<String> permitAllPaths = properties.getPermitAllPaths().stream().distinct().toList();
        http.authorizeHttpRequests(authorize -> {
            if (!permitAllPaths.isEmpty()) {
                authorize.requestMatchers(permitAllPaths.toArray(new String[0])).permitAll();
            }
            securityAnnotationSupportList.forEach(annotationSupport -> {
                List<AntPathRequestMatcher> antPatterns = annotationSupport.getAntPatterns();
                antPatterns.removeIf(matcher -> permitAllPaths.contains(matcher.getPattern()));
                if (!antPatterns.isEmpty()) {
                    authorize.requestMatchers(antPatterns.toArray(new AntPathRequestMatcher[0])).permitAll();
                }
            });
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
}
