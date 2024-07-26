package org.sunshine.security.jwt;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.sunshine.security.core.DefaultSecurityConfiguration;
import org.sunshine.security.core.handler.CommonAccessDeniedHandler;
import org.sunshine.security.core.support.AbstractSecurityAnnotationSupport;
import org.sunshine.security.jwt.filter.JwtAuthenticationFilter;
import org.sunshine.security.jwt.handler.JwtAuthenticationEntryPoint;
import org.sunshine.security.jwt.handler.JwtLogoutSuccessHandler;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.userdetails.JwtUserDetailsService;
import org.sunshine.security.jwt.util.JwtClaimsUtils;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Teamo
 * @since 2023/03/14
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtSecurityProperties.class)
@EnableMethodSecurity(securedEnabled = true)
@Import(DefaultSecurityConfiguration.class)
public class JwtSecurityConfiguration {

    private final JwtSecurityProperties jwtSecurityProperties;

    public JwtSecurityConfiguration(JwtSecurityProperties jwtSecurityProperties) {
        this.jwtSecurityProperties = jwtSecurityProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UserDetailsService userDetailsService,
                                                   CorsConfigurationSource corsConfigurationSource,
                                                   List<AbstractSecurityAnnotationSupport> securityAnnotationSupportList,
                                                   @Autowired(required = false) LogoutSuccessHandler logoutSuccessHandler) throws Exception {
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        List<AntPathRequestMatcher> antPathRequestMatchers = new ArrayList<>(16);

        List<String> permitAllPaths = jwtSecurityProperties.getPermitAllPaths().stream().distinct().collect(Collectors.toList());
        http.authorizeHttpRequests(authorize -> {
            if (!permitAllPaths.isEmpty()) {
                authorize.antMatchers(permitAllPaths.toArray(new String[0])).permitAll();
                antPathRequestMatchers.addAll(permitAllPaths.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList()));
            }
            securityAnnotationSupportList.forEach(annotationSupport -> {
                List<AntPathRequestMatcher> antPatterns = annotationSupport.getAntPatterns();
                antPatterns.removeIf(matcher -> permitAllPaths.contains(matcher.getPattern()));
                if (!antPatterns.isEmpty()) {
                    authorize.requestMatchers(antPatterns.toArray(new AntPathRequestMatcher[0])).permitAll();
                    antPathRequestMatchers.addAll(antPatterns);
                }
            });
            authorize.anyRequest().authenticated();
        });

        AuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();
        AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(userDetailsService, authenticationFailureHandler, jwtSecurityProperties);
        jwtAuthenticationFilter.setAntPathRequestMatchers(antPathRequestMatchers);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.userDetailsService(userDetailsService);

        if (logoutSuccessHandler != null) {
            http.logout(logoutConfigurer -> logoutConfigurer
                    .logoutUrl(jwtSecurityProperties.getLogoutPath())
                    .logoutSuccessHandler(logoutSuccessHandler));
        }

        http.exceptionHandling((exceptions) -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(new CommonAccessDeniedHandler())
        );

        http.csrf(AbstractHttpConfigurer::disable);

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
        urlBasedCorsConfigurationSource.getCorsConfigurations().forEach((s, configuration) -> {
            List<String> allowedHeaders = configuration.getAllowedHeaders();
            if (allowedHeaders != null && !allowedHeaders.contains(jwtSecurityProperties.getHeader())) {
                allowedHeaders.add(jwtSecurityProperties.getHeader());
            }
        });

        http.cors(cors -> cors.configurationSource(urlBasedCorsConfigurationSource));

        return http.build();
    }

    @Bean
    @ConditionalOnBean(JwtUserDetailsService.class)
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    public LogoutSuccessHandler logoutSuccessHandler(JwtUserDetailsService jwtUserDetailsService) {
        return new JwtLogoutSuccessHandler(jwtUserDetailsService);
    }

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public JwtClaimsUtils jwtClaimsUtils() {
        // Need to use JwtSecurityProperties, so register a bean
        JwtSecurityProperties.Secret secret = jwtSecurityProperties.getSecret();
        Assert.notNull(secret.getPublicKey(), "RSAPublicKey must not be null!");
        Assert.notNull(secret.getPrivateKey(), "RSAPrivateKey must not be null!");

        return new JwtClaimsUtils(jwtSecurityProperties);
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            PermitAll permitAll = Optional.ofNullable(handlerMethod.getMethodAnnotation(PermitAll.class)).orElseGet(() -> {
                Class<?> beanType = handlerMethod.getBeanType();
                return AnnotatedElementUtils.findMergedAnnotation(beanType, PermitAll.class);
            });
            if (permitAll == null) {
                String header = JwtClaimsUtils.getTokenRequestHeader();
                String tokenPrefix = JwtClaimsUtils.getTokenPrefix();
                @SuppressWarnings("rawtypes")
                Schema stringSchema = new StringSchema()._default(tokenPrefix).name(header).description("请求接口凭证");
                Parameter headerParameter = new HeaderParameter().name(header).description("请求接口凭证").schema(stringSchema);
                operation.addParametersItem(headerParameter);
            }
            return operation;
        };
    }
}
