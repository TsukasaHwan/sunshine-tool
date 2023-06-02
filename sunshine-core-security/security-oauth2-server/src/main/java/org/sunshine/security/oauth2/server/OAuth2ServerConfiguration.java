package org.sunshine.security.oauth2.server;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.sunshine.security.oauth2.server.authentication.OAuth2PasswordAuthenticationConverter;
import org.sunshine.security.oauth2.server.authentication.OAuth2PasswordAuthenticationProvider;
import org.sunshine.security.oauth2.server.authorization.OAuth2AuthConsentServiceImpl;
import org.sunshine.security.oauth2.server.authorization.OAuth2AuthServiceImpl;
import org.sunshine.security.oauth2.server.authorization.OAuth2ClientRepository;
import org.sunshine.security.oauth2.server.entity.OAuth2Auth;
import org.sunshine.security.oauth2.server.entity.OAuth2AuthConsent;
import org.sunshine.security.oauth2.server.entity.OAuth2Client;
import org.sunshine.security.oauth2.server.handler.OAuth2AuthenticationEntryPoint;
import org.sunshine.security.oauth2.server.properties.OAuth2ServerProperties;

import java.util.Arrays;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OAuth2ServerProperties.class)
public class OAuth2ServerConfiguration {

    private final OAuth2ServerProperties properties;

    public OAuth2ServerConfiguration(OAuth2ServerProperties properties) {
        this.properties = properties;
    }

    /**
     * OAuth2 配置，默认禁用OpenID Connect 1.0
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      OAuth2PasswordAuthenticationProvider passwordAuthenticationProvider) throws Exception {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

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
                                new OAuth2ClientCredentialsAuthenticationConverter(),
                                new OAuth2PasswordAuthenticationConverter()
                        )
                )
        ));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        http
                .requestMatcher(endpointsMatcher)
                .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .apply(authorizationServerConfigurer);

        http.authenticationProvider(passwordAuthenticationProvider);

        http.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(new OAuth2AuthenticationEntryPoint()));

        http.csrf().disable();

        http.headers().frameOptions().disable();

        return http.build();
    }

    /**
     * 客户端应用
     *
     * @return RegisteredClientRepository
     */
    @Bean
    @ConditionalOnMissingBean(RegisteredClientRepository.class)
    public RegisteredClientRepository registeredClientRepository(@Autowired(required = false) BaseMapper<OAuth2Client> oAuth2ClientMapper) {
        return new OAuth2ClientRepository(oAuth2ClientMapper);
    }

    /**
     * 令牌发放记录
     *
     * @param registeredClientRepository 注册客户端存储库
     * @return OAuth2AuthorizationService
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationService.class)
    public OAuth2AuthorizationService authorizationService(@Autowired(required = false) BaseMapper<OAuth2Auth> oAuth2AuthMapper,
                                                           RegisteredClientRepository registeredClientRepository) {
        return new OAuth2AuthServiceImpl(oAuth2AuthMapper, registeredClientRepository);
    }

    /**
     * 资源拥有者授权
     *
     * @param registeredClientRepository 注册客户端存储库
     * @return OAuth2AuthorizationConsentService
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationConsentService.class)
    public OAuth2AuthorizationConsentService authorizationConsentService(@Autowired(required = false) BaseMapper<OAuth2AuthConsent> oAuth2AuthConsentMapper,
                                                                         RegisteredClientRepository registeredClientRepository) {
        return new OAuth2AuthConsentServiceImpl(oAuth2AuthConsentMapper, registeredClientRepository);
    }

    /**
     * JWK资源
     *
     * @return JWKSource<SecurityContext>
     */
    @Bean
    @ConditionalOnMissingBean(JWKSource.class)
    public JWKSource<SecurityContext> jwkSource() {
        OAuth2ServerProperties.Secret secret = properties.getSecret();
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
     * @param jwtEncoder JwtEncoder
     * @return OAuth2TokenGenerator<?>
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2TokenGenerator.class)
    public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
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

    /**
     * OAuth2.1添加password模式支持
     *
     * @param authenticationManager {@link AuthenticationManager}
     * @param authorizationService  {@link OAuth2ServerConfiguration#authorizationService(BaseMapper, RegisteredClientRepository)}
     * @param tokenGenerator        {@link OAuth2ServerConfiguration#tokenGenerator(JwtEncoder)}
     * @return OAuth2PasswordAuthenticationProvider
     */
    @Bean
    public OAuth2PasswordAuthenticationProvider passwordAuthenticationProvider(AuthenticationManager authenticationManager,
                                                                               OAuth2AuthorizationService authorizationService,
                                                                               OAuth2TokenGenerator<?> tokenGenerator) {
        return new OAuth2PasswordAuthenticationProvider(authenticationManager, authorizationService, tokenGenerator);
    }
}
