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
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.sunshine.security.core.DefaultSecurityConfiguration;
import org.sunshine.security.core.handler.CommonAccessDeniedHandler;
import org.sunshine.security.core.support.PathPatternRequestMatcher;
import org.sunshine.security.core.support.PermitAllAnnotationExtractor;
import org.sunshine.security.core.support.SecurityAnnotationPathMatcherExtractor;
import org.sunshine.security.jwt.filter.JwtAuthenticationFilter;
import org.sunshine.security.jwt.handler.JwtAuthenticationEntryPoint;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.util.JwtUtils;

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
@Import({DefaultSecurityConfiguration.class, JwtSecurityComponent.class})
public class JwtSecurityConfiguration {

    private final JwtSecurityProperties jwtSecurityProperties;

    private final UserDetailsService userDetailsService;

    private final CorsConfigurationSource corsConfigurationSource;

    private List<SecurityAnnotationPathMatcherExtractor> securityAnnotationPathMatcherExtractors;

    private LogoutHandler logoutHandler;

    private LogoutSuccessHandler logoutSuccessHandler;

    public JwtSecurityConfiguration(JwtSecurityProperties jwtSecurityProperties,
                                    UserDetailsService userDetailsService,
                                    CorsConfigurationSource corsConfigurationSource) {
        this.jwtSecurityProperties = jwtSecurityProperties;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Autowired(required = false)
    public void setSecurityAnnotationPathMatcherExtractors(List<SecurityAnnotationPathMatcherExtractor> securityAnnotationPathMatcherExtractors) {
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
        this.applyPermitPathsIfAvailable(http);
        this.applyJwtSecurity(http);
        this.applyLogoutIfAvailable(http);
        this.applyCorsConfiguration(http);
        return http.build();
    }

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public JwtUtils jwtUtils() {
        // Need to use JwtSecurityProperties, so register a bean
        JwtSecurityProperties.Secret secret = jwtSecurityProperties.getSecret();
        Assert.notNull(secret.getPublicKey(), "RSAPublicKey must not be null!");
        Assert.notNull(secret.getPrivateKey(), "RSAPrivateKey must not be null!");

        return new JwtUtils(jwtSecurityProperties);
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
                String header = JwtUtils.getTokenHeader();
                String tokenPrefix = JwtUtils.getTokenPrefix();
                @SuppressWarnings("rawtypes")
                Schema stringSchema = new StringSchema()._default(tokenPrefix).name(header).description("请求接口凭证");
                Parameter headerParameter = new HeaderParameter().name(header).description("请求接口凭证").schema(stringSchema);
                operation.addParametersItem(headerParameter);
            }
            return operation;
        };
    }

    private void applyPermitPathsIfAvailable(HttpSecurity http) throws Exception {
        List<PathPatternRequestMatcher> requestMatchers =
                this.jwtSecurityProperties.getPermitAllPaths().stream()
                        .distinct()
                        .map(path -> PathPatternRequestMatcher.withDefaults().matcher(path))
                        .toList();
        http.authorizeHttpRequests(authorize -> {
            if (!requestMatchers.isEmpty()) {
                authorize.requestMatchers(requestMatchers.toArray(PathPatternRequestMatcher[]::new))
                        .permitAll();
            }
            securityAnnotationPathMatcherExtractors.forEach(extractor -> {
                if (extractor instanceof PermitAllAnnotationExtractor permitAllAnnotationExtractor) {
                    handlePermitAllAnnotationExtractor(authorize, permitAllAnnotationExtractor, requestMatchers);
                }
            });

            authorize.anyRequest().authenticated();
        });
    }

    private void handlePermitAllAnnotationExtractor(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize,
            PermitAllAnnotationExtractor permitAllAnnotationExtractor,
            List<PathPatternRequestMatcher> requestMatchers) {
        List<PathPatternRequestMatcher> matchers = permitAllAnnotationExtractor.getPathPatternRequestMatchers();
        matchers.removeIf(matcher -> requestMatchers.stream().anyMatch(p -> p.equals(matcher)));
        if (!matchers.isEmpty()) {
            authorize.requestMatchers(matchers.toArray(PathPatternRequestMatcher[]::new))
                    .permitAll();
        }
        matchers.addAll(requestMatchers);
    }

    private void applyJwtSecurity(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();
        AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                this.userDetailsService, authenticationFailureHandler, this.jwtSecurityProperties);
        jwtAuthenticationFilter.setExtractors(this.securityAnnotationPathMatcherExtractors);
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
