package org.sunshine.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.sunshine.security.core.DefaultSecurityConfiguration;
import org.sunshine.security.core.support.PermitAllAnnotationSupport;
import org.sunshine.security.jwt.filter.JwtAuthenticationFilter;
import org.sunshine.security.jwt.handler.JwtLogoutSuccessHandler;
import org.sunshine.security.jwt.handler.JwtTokenAccessDeniedHandler;
import org.sunshine.security.jwt.handler.JwtTokenAuthenticationEntryPoint;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.userdetails.JwtUserDetailsService;
import org.sunshine.security.jwt.util.JwtClaimsUtils;

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
@Import(DefaultSecurityConfiguration.class)
public class JwtSecurityAutoConfiguration {

    private final JwtSecurityProperties jwtSecurityProperties;

    public JwtSecurityAutoConfiguration(JwtSecurityProperties jwtSecurityProperties) {
        this.jwtSecurityProperties = jwtSecurityProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CorsConfigurationSource corsConfigurationSource,
                                                   PermitAllAnnotationSupport jwtPermitAllAnnotationSupport,
                                                   @Autowired(required = false) LogoutSuccessHandler logoutSuccessHandler) throws Exception {
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

        UserDetailsService userDetailsService = http.getSharedObject(AuthenticationManagerBuilder.class).getDefaultUserDetailsService();

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(userDetailsService, authenticationFailureHandler, jwtSecurityProperties);
        jwtAuthenticationFilter.setPermitAllAntPatterns(permitAllAntPatterns);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (logoutSuccessHandler != null) {
            http.logout().logoutUrl(jwtSecurityProperties.getLogoutPath()).logoutSuccessHandler(logoutSuccessHandler);
        }

        http.exceptionHandling((exceptions) -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(new JwtTokenAccessDeniedHandler())
        );

        // Turn off csrf protection
        http.csrf().disable();

        // Prevent iframe content from being displayed
        http.headers().frameOptions().disable();

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
        urlBasedCorsConfigurationSource.getCorsConfigurations().forEach((s, configuration) -> {
            List<String> allowedHeaders = configuration.getAllowedHeaders();
            if (allowedHeaders != null && !allowedHeaders.contains(jwtSecurityProperties.getHeader())) {
                allowedHeaders.add(jwtSecurityProperties.getHeader());
            }
        });

        // Turn on cross-domain support
        http.cors().configurationSource(urlBasedCorsConfigurationSource);

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
}
