package org.sunshine.security.jwt;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import jakarta.annotation.security.PermitAll;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.sunshine.security.core.DefaultSecurityConfiguration;
import org.sunshine.security.core.handler.CommonAccessDeniedHandler;
import org.sunshine.security.core.support.PermitAllAnnotationExtractor;
import org.sunshine.security.core.support.SecurityAnnotationPathMatcherExtractor;
import org.sunshine.security.jwt.filter.JwtAuthenticationFilter;
import org.sunshine.security.jwt.handler.JwtAuthenticationEntryPoint;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.util.JwtClaimsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private final UserDetailsService userDetailsService;

    private final CorsConfigurationSource corsConfigurationSource;

    private final List<SecurityAnnotationPathMatcherExtractor> securityAnnotationPathMatcherExtractors;

    private LogoutHandler logoutHandler;

    private LogoutSuccessHandler logoutSuccessHandler;

    public JwtSecurityConfiguration(JwtSecurityProperties jwtSecurityProperties,
                                    UserDetailsService userDetailsService,
                                    CorsConfigurationSource corsConfigurationSource,
                                    List<SecurityAnnotationPathMatcherExtractor> securityAnnotationPathMatcherExtractors) {
        this.jwtSecurityProperties = jwtSecurityProperties;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
        this.securityAnnotationPathMatcherExtractors = securityAnnotationPathMatcherExtractors;
    }

    @Autowired(required = false)
    public void setLogoutHandler(LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    @Autowired(required = false)
    public void setLogoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        List<AntPathRequestMatcher> permitAllMatchers = this.applyPermitPathsIfAvailable(http);
        this.applyJwtSecurity(http, permitAllMatchers);
        this.applyLogoutIfAvailable(http);
        this.applyCorsConfiguration(http);
        return http.build();
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
    @Primary
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            boolean empty = Optional.ofNullable(handlerMethod.getMethodAnnotation(PermitAll.class)).or(() -> {
                Class<?> beanType = handlerMethod.getBeanType();
                return Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(beanType, PermitAll.class));
            }).isEmpty();
            if (empty) {
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

    private List<AntPathRequestMatcher> applyPermitPathsIfAvailable(HttpSecurity http) throws Exception {
        List<AntPathRequestMatcher> permitAllMatchers = new ArrayList<>(16);
        List<String> permitAllPaths = this.jwtSecurityProperties.getPermitAllPaths().stream().distinct().toList();
        http.authorizeHttpRequests(authorize -> {
            if (!permitAllPaths.isEmpty()) {
                List<AntPathRequestMatcher> requestMatchers = permitAllPaths.stream()
                        .map(AntPathRequestMatcher::antMatcher)
                        .toList();
                authorize.requestMatchers(requestMatchers.toArray(AntPathRequestMatcher[]::new))
                        .permitAll();
                permitAllMatchers.addAll(requestMatchers);
            }
            securityAnnotationPathMatcherExtractors.forEach(extractor -> {
                if (extractor instanceof PermitAllAnnotationExtractor permitAllAnnotationExtractor) {
                    List<AntPathRequestMatcher> permitAllAnnotationMatchers = handlePermitAllAnnotationExtractor(authorize, permitAllAnnotationExtractor, permitAllPaths);
                    permitAllMatchers.addAll(permitAllAnnotationMatchers);
                }
            });
            authorize.anyRequest().authenticated();
        });
        return permitAllMatchers;
    }

    private List<AntPathRequestMatcher> handlePermitAllAnnotationExtractor(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize,
            PermitAllAnnotationExtractor permitAllAnnotationExtractor,
            List<String> permitAllPaths) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        List<AntPathRequestMatcher> antPatterns = permitAllAnnotationExtractor.getAntPatterns();
        antPatterns.removeIf(matcher -> {
            String pattern = matcher.getPattern();
            return permitAllPaths.stream().anyMatch(p -> antPathMatcher.match(p, pattern));
        });
        if (!antPatterns.isEmpty()) {
            authorize.requestMatchers(antPatterns.toArray(AntPathRequestMatcher[]::new))
                    .permitAll();
        }
        return antPatterns;
    }

    private void applyJwtSecurity(HttpSecurity http, List<AntPathRequestMatcher> permitAllMatchers) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();
        AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                this.userDetailsService, authenticationFailureHandler, this.jwtSecurityProperties);
        jwtAuthenticationFilter.setPermitAllMatchers(permitAllMatchers);
        // @formatter:off
        http
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .userDetailsService(this.userDetailsService)
            .exceptionHandling((exceptions) -> exceptions
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(new CommonAccessDeniedHandler())
            );
        // @formatter:on
    }

    private void applyLogoutIfAvailable(HttpSecurity http) throws Exception {
        http.logout(logoutConfigurer -> {
            String logoutUrl = this.jwtSecurityProperties.getLogoutUrl();
            if (logoutUrl != null && !logoutUrl.isBlank()) {
                logoutConfigurer.logoutUrl(logoutUrl);
            }
            if (this.logoutHandler != null) {
                logoutConfigurer.addLogoutHandler(this.logoutHandler);
            }
            if (this.logoutSuccessHandler != null) {
                logoutConfigurer.logoutSuccessHandler(this.logoutSuccessHandler);
            }
        });
    }

    private void applyCorsConfiguration(HttpSecurity http) throws Exception {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = (UrlBasedCorsConfigurationSource) this.corsConfigurationSource;
        String header = this.jwtSecurityProperties.getHeader();
        urlBasedCorsConfigurationSource.getCorsConfigurations().forEach((s, configuration) -> {
            List<String> allowedHeaders = configuration.getAllowedHeaders();
            if (allowedHeaders == null) {
                allowedHeaders = new ArrayList<>(1);
            }
            if (!allowedHeaders.contains(header)) {
                allowedHeaders.add(header);
            }
            configuration.setAllowedHeaders(allowedHeaders);
        });

        http.cors(cors -> cors.configurationSource(urlBasedCorsConfigurationSource));
    }
}
