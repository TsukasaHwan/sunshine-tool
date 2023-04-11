package org.sunshine.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.sunshine.core.security.authentication.NoPasswordAuthenticationProvider;
import org.sunshine.core.security.context.TransmittableThreadLocalSecurityContextHolderStrategy;
import org.sunshine.core.security.filter.JwtAuthenticationFilter;
import org.sunshine.core.security.handler.JwtLogoutSuccessHandler;
import org.sunshine.core.security.handler.JwtTokenAccessDeniedHandler;
import org.sunshine.core.security.handler.JwtTokenAuthenticationEntryPoint;
import org.sunshine.core.security.properties.JwtSecurityProperties;
import org.sunshine.core.security.support.JwtPermitAllAnnotationSupport;
import org.sunshine.core.security.userdetails.JwtUserDetailsService;
import org.sunshine.core.security.util.JwtClaimsUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Teamo
 * @since 2023/03/14
 */
@AutoConfiguration(before = SecurityAutoConfiguration.class)
@EnableConfigurationProperties(JwtSecurityProperties.class)
@ConditionalOnProperty(value = "jwt.security.enable", havingValue = "true")
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class JwtSecurityAutoConfiguration {

    private final JwtSecurityProperties jwtSecurityProperties;

    public JwtSecurityAutoConfiguration(JwtSecurityProperties jwtSecurityProperties) {
        this.jwtSecurityProperties = jwtSecurityProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UserDetailsService userDetailsService,
                                                   CorsConfigurationSource corsConfigurationSource,
                                                   JwtPermitAllAnnotationSupport jwtPermitAllAnnotationSupport,
                                                   @Autowired(required = false) LogoutSuccessHandler logoutSuccessHandler,
                                                   @Autowired(required = false) AuthenticationProvider authenticationProvider) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        Set<String> permitAllAntPatterns = new LinkedHashSet<>();

        List<String> permitAllPaths = jwtSecurityProperties.getPermitAllPaths();
        if (!permitAllPaths.isEmpty()) {
            http.authorizeHttpRequests().antMatchers(permitAllPaths.toArray(new String[0])).permitAll();
            permitAllAntPatterns.addAll(permitAllPaths);
        }

        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry = http.authorizeHttpRequests();
        jwtPermitAllAnnotationSupport.getAntPatterns().forEach((httpMethod, antPatterns) -> {
            Set<String> antPatternsSet = new LinkedHashSet<>(antPatterns);
            antPatternsSet.removeIf(permitAllPaths::contains);
            if (!antPatternsSet.isEmpty()) {
                registry.antMatchers(httpMethod, antPatternsSet.toArray(new String[0])).permitAll();
                permitAllAntPatterns.addAll(antPatternsSet);
            }
        });

        http.authorizeHttpRequests().anyRequest().authenticated();

        AuthenticationEntryPoint authenticationEntryPoint = new JwtTokenAuthenticationEntryPoint();
        AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(userDetailsService, authenticationFailureHandler, jwtSecurityProperties);
        jwtAuthenticationFilter.setPermitAllAntPatterns(permitAllAntPatterns);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (logoutSuccessHandler != null) {
            http.logout().logoutUrl(jwtSecurityProperties.getLogoutPath()).logoutSuccessHandler(logoutSuccessHandler);
        }

        if (authenticationProvider != null) {
            http.authenticationProvider(authenticationProvider);
        }

        http.exceptionHandling((exceptions) -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(new JwtTokenAccessDeniedHandler())
        );

        // Turn off csrf protection
        http.csrf().disable();

        // Prevent iframe content from being displayed
        http.headers().frameOptions().disable();

        // Turn on cross-domain support
        http.cors().configurationSource(corsConfigurationSource);

        return http.build();
    }

    @Bean
    @ConditionalOnBean(JwtUserDetailsService.class)
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    public LogoutSuccessHandler logoutSuccessHandler(JwtUserDetailsService jwtUserDetailsService) {
        return new JwtLogoutSuccessHandler(jwtUserDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
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
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
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
                jwtSecurityProperties.getHeader(),
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

    @Bean
    public JwtPermitAllAnnotationSupport permitAllAnnotationSupport() {
        return new JwtPermitAllAnnotationSupport();
    }

    @Bean
    public MethodInvokingFactoryBean securityContextHolderMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(TransmittableThreadLocalSecurityContextHolderStrategy.class.getName());
        return methodInvokingFactoryBean;
    }

    @Bean
    @ConditionalOnProperty(value = "jwt.security.enablePasswordAuthentication", havingValue = "false")
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        NoPasswordAuthenticationProvider authenticationProvider = new NoPasswordAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.afterPropertiesSet();
        return authenticationProvider;
    }
}
