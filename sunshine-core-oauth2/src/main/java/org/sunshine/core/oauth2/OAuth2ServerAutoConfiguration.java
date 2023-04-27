package org.sunshine.core.oauth2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.sunshine.core.oauth2.authorization.OAuth2AuthConsentServiceImpl;
import org.sunshine.core.oauth2.authorization.OAuth2AuthServiceImpl;
import org.sunshine.core.oauth2.authorization.OAuth2ClientRepository;
import org.sunshine.core.oauth2.entity.OAuth2Auth;
import org.sunshine.core.oauth2.entity.OAuth2AuthConsent;
import org.sunshine.core.oauth2.entity.OAuth2Client;
import org.sunshine.core.oauth2.properties.OAuth2ServerProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * @author Teamo
 * @since 2023/4/26
 */
@AutoConfiguration(before = SecurityAutoConfiguration.class)
@EnableConfigurationProperties(OAuth2ServerProperties.class)
@ConditionalOnProperty(value = "spring.oauth2.server.enable", havingValue = "true")
public class OAuth2ServerAutoConfiguration {

    private final OAuth2ServerProperties oAuth2ServerProperties;

    public OAuth2ServerAutoConfiguration(OAuth2ServerProperties oAuth2ServerProperties) {
        this.oAuth2ServerProperties = oAuth2ServerProperties;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        authorizationServerConfigurer
                // Enable OpenID Connect 1.0
                .oidc(Customizer.withDefaults());

        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        http
                .requestMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .apply(authorizationServerConfigurer);

        http.csrf().disable();
        return http.build();
    }

    /**
     * 客户端存储库
     *
     * @return RegisteredClientRepository
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(@Autowired(required = false) BaseMapper<OAuth2Client> oAuth2ClientMapper) {
        return new OAuth2ClientRepository(oAuth2ClientMapper);
    }

    /**
     * 使用数据库记录认证记录
     *
     * @param registeredClientRepository 注册客户端存储库
     * @return OAuth2AuthorizationService
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(@Autowired(required = false) BaseMapper<OAuth2Auth> oAuth2AuthMapper,
                                                           RegisteredClientRepository registeredClientRepository) {
        return new OAuth2AuthServiceImpl(oAuth2AuthMapper, registeredClientRepository);
    }

    /**
     * 使用数据库记录授权记录
     *
     * @param registeredClientRepository 注册客户端存储库
     * @return OAuth2AuthorizationConsentService
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(@Autowired(required = false) BaseMapper<OAuth2AuthConsent> oAuth2AuthConsentMapper,
                                                                         RegisteredClientRepository registeredClientRepository) {
        return new OAuth2AuthConsentServiceImpl(oAuth2AuthConsentMapper, registeredClientRepository);
    }

    /**
     * jwk源
     *
     * @return JWKSource<SecurityContext>
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAPublicKey publicKey = oAuth2ServerProperties.getPublicKey();
        RSAPrivateKey privateKey = oAuth2ServerProperties.getPrivateKey();
        Assert.notNull(publicKey, "RSAPublicKey must not be null!");
        Assert.notNull(privateKey, "RSAPrivateKey must not be null!");

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * jwt解码器
     *
     * @param jwkSource jwk源
     * @return JwtEncoder
     */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * jwt编码器
     *
     * @param jwkSource jwk源
     * @return JwtDecoder
     */
    @Bean
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
    public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder, OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(jwtCustomizer);
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    /**
     * 自定义JWT字段
     *
     * @return OAuth2TokenCustomizer<JwtEncodingContext>
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwsHeader.Builder headers = context.getJwsHeader();
            JwtClaimsSet.Builder claims = context.getClaims();
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                // Customize headers/claims for access_token
                headers.header("customerHeader", "这是一个自定义header");
                claims.claim("customerClaim", "这是一个自定义Claim");
            } else if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
                // Customize headers/claims for id_token
            }
        };
    }

    /**
     * 使用默认认证端点
     *
     * @return AuthorizationServerSettings
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
