package org.sunshine.core.security.oauth2.authorization.server;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.sunshine.core.security.oauth2.authorization.server.properties.OAuth2AuthorizationServerProperties;
import org.sunshine.core.security.core.authentication.CommonAuthenticationEntryPoint;
import org.sunshine.core.security.core.enums.RoleEnum;
import org.sunshine.core.security.core.oauth2.TokenConstant;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OAuth2AuthorizationServerProperties.class)
public class AuthorizationServerConfiguration {

    private final OAuth2AuthorizationServerProperties properties;

    public AuthorizationServerConfiguration(OAuth2AuthorizationServerProperties properties) {
        this.properties = properties;
    }

    /**
     * OAuth2 配置，使用 OAuth 2.1 推荐的授权码模式
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        http.sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();

        if (properties.getConsentPageUri() != null) {
            authorizationServerConfigurer.authorizationEndpoint(authorizationEndpoint ->
                    authorizationEndpoint.consentPage(properties.getConsentPageUri()));
        }

        authorizationServerConfigurer.tokenEndpoint(tokenEndpoint -> tokenEndpoint.accessTokenRequestConverter(
                new DelegatingAuthenticationConverter(
                        Arrays.asList(
                                new OAuth2AuthorizationCodeAuthenticationConverter(),
                                new OAuth2RefreshTokenAuthenticationConverter(),
                                new OAuth2ClientCredentialsAuthenticationConverter()
                        )
                )
        ));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        http
                .securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwtConfigurer -> {
                }))
                .with(authorizationServerConfigurer, oAuth2AuthorizationServerConfigurer -> {
                });

        http.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(new CommonAuthenticationEntryPoint()));

        http.csrf(AbstractHttpConfigurer::disable);

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    /**
     * JWK资源
     *
     * @return JWKSource<SecurityContext>
     */
    @Bean
    @ConditionalOnMissingBean(JWKSource.class)
    public JWKSource<SecurityContext> jwkSource() {
        OAuth2AuthorizationServerProperties.Secret secret = properties.getSecret();
        Assert.notNull(secret.getPublicKey(), "RSAPublicKey must not be null!");
        Assert.notNull(secret.getPrivateKey(), "RSAPrivateKey must not be null!");

        RSAKey rsaKey = new RSAKey.Builder(secret.getPublicKey())
                .privateKey(secret.getPrivateKey())
                .keyID(secret.getKeyId())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * JWT解码器
     *
     * @param jwkSource JWK资源
     * @return JwtEncoder
     */
    @Bean
    @ConditionalOnMissingBean(JwtEncoder.class)
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * JWT编码器
     *
     * @param jwkSource JWK资源
     * @return JwtDecoder
     */
    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * token生成器
     *
     * @param jwtEncoder    JwtEncoder
     * @param jwtCustomizer OAuth2TokenCustomizer<JwtEncodingContext>
     * @return OAuth2TokenGenerator<?>
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2TokenGenerator.class)
    public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder,
                                                  OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(jwtCustomizer);
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    /**
     * 自定义token
     *
     * @return OAuth2TokenCustomizer<JwtEncodingContext>
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2TokenCustomizer.class)
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            Authentication authentication = context.getPrincipal();
            if (authentication instanceof UsernamePasswordAuthenticationToken && authentication.getPrincipal() instanceof UserDetails userDetails) {
                Set<String> authorities = userDetails.getAuthorities().stream().map(grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    if (authority.startsWith(RoleEnum.RoleCode.ROLE_PREFIX)) {
                        return authority.replace(RoleEnum.RoleCode.ROLE_PREFIX, "");
                    }
                    return authority;
                }).collect(Collectors.toSet());
                context.getClaims().claim(TokenConstant.AUTHORITIES, authorities);
            }
        };
    }

    /**
     * 认证端点
     * <pre>
     * authorizationEndpoint("/oauth2/authorize");
     * tokenEndpoint("/oauth2/token");
     * jwkSetEndpoint("/oauth2/jwks");
     * tokenRevocationEndpoint("/oauth2/revoke");
     * tokenIntrospectionEndpoint("/oauth2/introspect");
     * oidcClientRegistrationEndpoint("/connect/register");
     * oidcUserInfoEndpoint("/userinfo");
     * </pre>
     *
     * @return AuthorizationServerSettings
     */
    @Bean
    @ConditionalOnMissingBean(AuthorizationServerSettings.class)
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

}
