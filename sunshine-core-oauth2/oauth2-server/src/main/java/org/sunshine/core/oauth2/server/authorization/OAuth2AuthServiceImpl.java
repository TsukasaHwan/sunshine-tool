package org.sunshine.core.oauth2.server.authorization;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.sunshine.core.oauth2.server.entity.OAuth2Auth;
import org.sunshine.core.tool.util.ObjectUtils;
import org.sunshine.core.tool.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Teamo
 * @since 2023/4/26
 */
public class OAuth2AuthServiceImpl implements OAuth2AuthorizationService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BaseMapper<OAuth2Auth> oAuth2AuthMapper;

    private final RegisteredClientRepository registeredClientRepository;

    public OAuth2AuthServiceImpl(BaseMapper<OAuth2Auth> oAuth2AuthMapper,
                                 RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(oAuth2AuthMapper, "oAuth2AuthMapper cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.oAuth2AuthMapper = oAuth2AuthMapper;
        this.registeredClientRepository = registeredClientRepository;

        ClassLoader classLoader = OAuth2AuthServiceImpl.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        OAuth2Authorization existingAuthorization = findById(authorization.getId());
        if (ObjectUtils.isEmpty(existingAuthorization)) {
            insertAuth(authorization);
        } else {
            updateAuth(authorization);
        }
    }

    private void insertAuth(OAuth2Authorization authorization) {
        OAuth2Auth oAuth2Auth = convert(authorization);
        oAuth2AuthMapper.insert(oAuth2Auth);
    }

    private void updateAuth(OAuth2Authorization authorization) {
        OAuth2Auth oAuth2Auth = convert(authorization);
        oAuth2AuthMapper.updateById(oAuth2Auth);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        oAuth2AuthMapper.deleteById(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return findBy(Wrappers.<OAuth2Auth>lambdaQuery().eq(OAuth2Auth::getId, id));
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        if (ObjectUtils.isEmpty(tokenType)) {
            LambdaQueryWrapper<OAuth2Auth> wrapper = Wrappers.<OAuth2Auth>lambdaQuery()
                    .eq(OAuth2Auth::getState, token)
                    .or()
                    .eq(OAuth2Auth::getAuthorizationCodeValue, token)
                    .or()
                    .eq(OAuth2Auth::getAccessTokenValue, token)
                    .or()
                    .eq(OAuth2Auth::getRefreshTokenValue, token);
            return findBy(wrapper);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            LambdaQueryWrapper<OAuth2Auth> wrapper = Wrappers.<OAuth2Auth>lambdaQuery()
                    .eq(OAuth2Auth::getState, token);
            return findBy(wrapper);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            LambdaQueryWrapper<OAuth2Auth> wrapper = Wrappers.<OAuth2Auth>lambdaQuery()
                    .eq(OAuth2Auth::getAuthorizationCodeValue, token);
            return findBy(wrapper);
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            LambdaQueryWrapper<OAuth2Auth> wrapper = Wrappers.<OAuth2Auth>lambdaQuery()
                    .eq(OAuth2Auth::getAccessTokenValue, token);
            return findBy(wrapper);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            LambdaQueryWrapper<OAuth2Auth> wrapper = Wrappers.<OAuth2Auth>lambdaQuery()
                    .eq(OAuth2Auth::getRefreshTokenValue, token);
            return findBy(wrapper);
        }
        return null;
    }

    private OAuth2Authorization findBy(LambdaQueryWrapper<OAuth2Auth> wrapper) {
        OAuth2Auth oAuth2Auth = oAuth2AuthMapper.selectOne(wrapper);
        return convert(oAuth2Auth);
    }

    private OAuth2Auth convert(OAuth2Authorization authorization) {
        OAuth2Auth oAuth2Auth = new OAuth2Auth();
        oAuth2Auth.setId(authorization.getId());
        oAuth2Auth.setRegisteredClientId(authorization.getRegisteredClientId());
        oAuth2Auth.setPrincipalName(authorization.getPrincipalName());
        oAuth2Auth.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        oAuth2Auth.setAuthorizedScopes(StringUtils.collectionToCommaDelimitedString(authorization.getAuthorizedScopes()));
        oAuth2Auth.setAttributes(writeMap(authorization.getAttributes()));
        String state = null;
        String authorizationState = authorization.getAttribute(OAuth2ParameterNames.STATE);
        if (StringUtils.isNotBlank(authorizationState)) {
            state = authorizationState;
        }
        oAuth2Auth.setState(state);
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);

        applyTokenColum(oAuth2Auth, authorizationCode);

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        applyTokenColum(oAuth2Auth, accessToken);

        String accessTokenType = null;
        String accessTokenScopes = null;
        if (ObjectUtils.isNotEmpty(accessToken)) {
            accessTokenType = accessToken.getToken().getTokenType().getValue();
            if (!CollectionUtils.isEmpty(accessToken.getToken().getScopes())) {
                accessTokenScopes = StringUtils.collectionToCommaDelimitedString(accessToken.getToken().getScopes());
            }
        }
        oAuth2Auth.setAccessTokenType(accessTokenType);
        oAuth2Auth.setAccessTokenScopes(accessTokenScopes);
        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        applyTokenColum(oAuth2Auth, oidcIdToken);

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();
        applyTokenColum(oAuth2Auth, refreshToken);

        return oAuth2Auth;
    }

    private OAuth2Authorization convert(OAuth2Auth oAuth2Auth) {
        if (oAuth2Auth == null) {
            return null;
        }
        String registeredClientId = oAuth2Auth.getRegisteredClientId();
        RegisteredClient registeredClient = this.registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + registeredClientId + "' was not found in the RegisteredClientRepository.");
        }
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);

        Set<String> authorizedScopes = Collections.emptySet();
        String authorizedScopesString = oAuth2Auth.getAuthorizedScopes();
        if (authorizedScopesString != null) {
            authorizedScopes = StringUtils.commaDelimitedListToSet(authorizedScopesString);
        }

        builder.id(oAuth2Auth.getId())
                .principalName(oAuth2Auth.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(oAuth2Auth.getAuthorizationGrantType()))
                .authorizedScopes(authorizedScopes)
                .attributes((attrs) -> attrs.putAll(parseMap(oAuth2Auth.getAttributes())));

        String state = oAuth2Auth.getState();
        if (StringUtils.isNotBlank(state)) {
            builder.attribute(OAuth2ParameterNames.STATE, state);
        }
        Instant tokenIssuedAt;
        Instant tokenExpiresAt;
        String authorizationCodeValue = oAuth2Auth.getAuthorizationCodeValue();

        if (StringUtils.isNotBlank(authorizationCodeValue)) {
            tokenIssuedAt = oAuth2Auth.getAuthorizationCodeIssuedAt().atZone(ZoneId.systemDefault()).toInstant();
            tokenExpiresAt = oAuth2Auth.getAuthorizationCodeExpiresAt().atZone(ZoneId.systemDefault()).toInstant();
            Map<String, Object> authorizationCodeMetadata = parseMap(oAuth2Auth.getAuthorizationCodeMetadata());

            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    authorizationCodeValue, tokenIssuedAt, tokenExpiresAt);
            builder.token(authorizationCode, (metadata) -> metadata.putAll(authorizationCodeMetadata));
        }

        String accessTokenValue = oAuth2Auth.getAccessTokenValue();
        if (StringUtils.isNotBlank(accessTokenValue)) {
            tokenIssuedAt = oAuth2Auth.getAccessTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant();
            tokenExpiresAt = oAuth2Auth.getAccessTokenExpiresAt().atZone(ZoneId.systemDefault()).toInstant();
            Map<String, Object> accessTokenMetadata = parseMap(oAuth2Auth.getAccessTokenMetadata());
            OAuth2AccessToken.TokenType tokenType = null;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(oAuth2Auth.getAccessTokenType())) {
                tokenType = OAuth2AccessToken.TokenType.BEARER;
            }

            Set<String> scopes = Collections.emptySet();
            String accessTokenScopes = oAuth2Auth.getAccessTokenScopes();
            if (ObjectUtils.isNotEmpty(accessTokenScopes)) {
                scopes = StringUtils.commaDelimitedListToSet(accessTokenScopes);
            }
            OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, accessTokenValue, tokenIssuedAt, tokenExpiresAt, scopes);
            builder.token(accessToken, (metadata) -> metadata.putAll(accessTokenMetadata));
        }

        String oidcIdTokenValue = oAuth2Auth.getOidcIdTokenValue();
        if (StringUtils.isNotBlank(oidcIdTokenValue)) {
            tokenIssuedAt = oAuth2Auth.getOidcIdTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant();
            tokenExpiresAt = oAuth2Auth.getOidcIdTokenExpiresAt().atZone(ZoneId.systemDefault()).toInstant();
            Map<String, Object> oidcTokenMetadata = parseMap(oAuth2Auth.getOidcIdTokenMetadata());

            @SuppressWarnings("unchecked")
            OidcIdToken oidcToken = new OidcIdToken(
                    oidcIdTokenValue, tokenIssuedAt, tokenExpiresAt, (Map<String, Object>) oidcTokenMetadata.get(OAuth2Authorization.Token.CLAIMS_METADATA_NAME));
            builder.token(oidcToken, (metadata) -> metadata.putAll(oidcTokenMetadata));
        }

        String refreshTokenValue = oAuth2Auth.getRefreshTokenValue();
        if (StringUtils.isNotBlank(refreshTokenValue)) {
            tokenIssuedAt = oAuth2Auth.getRefreshTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant();
            tokenExpiresAt = null;
            LocalDateTime refreshTokenExpiresAt = oAuth2Auth.getRefreshTokenExpiresAt();
            if (refreshTokenExpiresAt != null) {
                tokenExpiresAt = refreshTokenExpiresAt.atZone(ZoneId.systemDefault()).toInstant();
            }
            Map<String, Object> refreshTokenMetadata = parseMap(oAuth2Auth.getRefreshTokenMetadata());

            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    refreshTokenValue, tokenIssuedAt, tokenExpiresAt);
            builder.token(refreshToken, (metadata) -> metadata.putAll(refreshTokenMetadata));
        }

        return builder.build();
    }

    private <T extends OAuth2Token> void applyTokenColum(OAuth2Auth oAuth2Auth, OAuth2Authorization.Token<T> token) {
        String tokenValue = null;
        LocalDateTime tokenIssuedAt = null;
        LocalDateTime tokenExpiresAt = null;
        String metadata = null;
        if (ObjectUtils.isEmpty(token)) {
            return;
        }

        T t = token.getToken();
        tokenValue = t.getTokenValue();
        if (ObjectUtils.isNotEmpty(t.getIssuedAt())) {
            tokenIssuedAt = LocalDateTime.ofInstant(t.getIssuedAt(), ZoneId.systemDefault());
        }
        if (ObjectUtils.isNotEmpty(t.getExpiresAt())) {
            tokenExpiresAt = LocalDateTime.ofInstant(t.getExpiresAt(), ZoneId.systemDefault());
        }
        metadata = writeMap(token.getMetadata());

        if (t instanceof OAuth2AuthorizationCode) {
            oAuth2Auth.setAuthorizationCodeValue(tokenValue);
            oAuth2Auth.setAuthorizationCodeIssuedAt(tokenIssuedAt);
            oAuth2Auth.setAuthorizationCodeExpiresAt(tokenExpiresAt);
            oAuth2Auth.setAuthorizationCodeMetadata(metadata);
        } else if (t instanceof OAuth2AccessToken) {
            oAuth2Auth.setAccessTokenValue(tokenValue);
            oAuth2Auth.setAccessTokenIssuedAt(tokenIssuedAt);
            oAuth2Auth.setAccessTokenExpiresAt(tokenExpiresAt);
            oAuth2Auth.setAccessTokenMetadata(metadata);
        } else if (t instanceof OidcIdToken) {
            oAuth2Auth.setOidcIdTokenValue(tokenValue);
            oAuth2Auth.setOidcIdTokenIssuedAt(tokenIssuedAt);
            oAuth2Auth.setOidcIdTokenExpiresAt(tokenExpiresAt);
            oAuth2Auth.setOidcIdTokenMetadata(metadata);
        } else if (t instanceof OAuth2RefreshToken) {
            oAuth2Auth.setRefreshTokenValue(tokenValue);
            oAuth2Auth.setRefreshTokenIssuedAt(tokenIssuedAt);
            oAuth2Auth.setRefreshTokenExpiresAt(tokenExpiresAt);
            oAuth2Auth.setRefreshTokenMetadata(metadata);
        }
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
