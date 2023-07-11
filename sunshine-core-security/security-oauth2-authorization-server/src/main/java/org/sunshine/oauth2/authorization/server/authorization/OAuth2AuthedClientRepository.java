package org.sunshine.oauth2.authorization.server.authorization;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;
import org.sunshine.core.tool.util.BeanUtils;
import org.sunshine.core.tool.util.ObjectUtils;
import org.sunshine.core.tool.util.StringUtils;
import org.sunshine.oauth2.authorization.server.entity.OAuth2AuthedClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Teamo
 * @since 2023/4/26
 */
public class OAuth2AuthedClientRepository implements RegisteredClientRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BaseMapper<OAuth2AuthedClient> oAuth2AuthedClientMapper;

    public OAuth2AuthedClientRepository(BaseMapper<OAuth2AuthedClient> oAuth2AuthedClientMapper) {
        Assert.notNull(oAuth2AuthedClientMapper, "oAuth2AuthedClientMapper cannot be null");
        this.oAuth2AuthedClientMapper = oAuth2AuthedClientMapper;

        ClassLoader classLoader = OAuth2AuthedClientRepository.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        OAuth2AuthedClient oAuth2AuthedClient = oAuth2AuthedClientMapper.selectById(registeredClient.getId());
        if (ObjectUtils.isEmpty(oAuth2AuthedClient)) {
            insertClient(registeredClient);
        } else {
            updateClient(registeredClient);
        }
    }

    private void updateClient(RegisteredClient registeredClient) {
        List<String> clientAuthenticationMethods = getClientAuthenticationMethods(registeredClient);
        List<String> authorizationGrantTypes = getAuthorizationGrantTypes(registeredClient);

        LambdaUpdateWrapper<OAuth2AuthedClient> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper
                .eq(OAuth2AuthedClient::getId, registeredClient.getId())
                .set(OAuth2AuthedClient::getClientName, registeredClient.getClientName())
                .set(OAuth2AuthedClient::getClientAuthenticationMethods, StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods))
                .set(OAuth2AuthedClient::getAuthorizationGrantTypes, StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes))
                .set(OAuth2AuthedClient::getRedirectUris, StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()))
                .set(OAuth2AuthedClient::getScopes, StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()))
                .set(OAuth2AuthedClient::getClientSettings, writeMap(registeredClient.getClientSettings().getSettings()))
                .set(OAuth2AuthedClient::getTokenSettings, writeMap(registeredClient.getTokenSettings().getSettings()));
        oAuth2AuthedClientMapper.update(null, updateWrapper);
    }

    private void insertClient(RegisteredClient registeredClient) {
        assertUniqueIdentifiers(registeredClient);
        OAuth2AuthedClient oAuth2AuthedClient = new OAuth2AuthedClient();
        BeanUtils.copyProperties(registeredClient, oAuth2AuthedClient);
        if (ObjectUtils.isNotEmpty(registeredClient.getClientIdIssuedAt())) {
            oAuth2AuthedClient.setClientIdIssuedAt(LocalDateTime.ofInstant(registeredClient.getClientIdIssuedAt(), ZoneId.systemDefault()));
        }
        if (ObjectUtils.isNotEmpty(registeredClient.getClientSecretExpiresAt())) {
            oAuth2AuthedClient.setClientSecretExpiresAt(LocalDateTime.ofInstant(registeredClient.getClientSecretExpiresAt(), ZoneId.systemDefault()));
        }
        List<String> clientAuthenticationMethods = getClientAuthenticationMethods(registeredClient);
        List<String> authorizationGrantTypes = getAuthorizationGrantTypes(registeredClient);

        oAuth2AuthedClient.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        oAuth2AuthedClient.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
        oAuth2AuthedClient.setRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        oAuth2AuthedClient.setScopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));
        oAuth2AuthedClient.setClientSettings(writeMap(registeredClient.getClientSettings().getSettings()));
        oAuth2AuthedClient.setTokenSettings(writeMap(registeredClient.getTokenSettings().getSettings()));
        oAuth2AuthedClientMapper.insert(oAuth2AuthedClient);
    }

    private List<String> getClientAuthenticationMethods(RegisteredClient registeredClient) {
        List<String> clientAuthenticationMethods = new ArrayList<>(registeredClient.getClientAuthenticationMethods().size());
        registeredClient.getClientAuthenticationMethods().forEach(clientAuthenticationMethod ->
                clientAuthenticationMethods.add(clientAuthenticationMethod.getValue()));
        return clientAuthenticationMethods;
    }

    private List<String> getAuthorizationGrantTypes(RegisteredClient registeredClient) {
        List<String> authorizationGrantTypes = new ArrayList<>(registeredClient.getAuthorizationGrantTypes().size());
        registeredClient.getAuthorizationGrantTypes().forEach(authorizationGrantType ->
                authorizationGrantTypes.add(authorizationGrantType.getValue()));
        return authorizationGrantTypes;
    }

    private void assertUniqueIdentifiers(RegisteredClient registeredClient) {
        Long count = oAuth2AuthedClientMapper.selectCount(Wrappers.<OAuth2AuthedClient>lambdaQuery().eq(OAuth2AuthedClient::getClientId, registeredClient.getClientId()));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("Registered client must be unique. " +
                                               "Found duplicate client identifier: " + registeredClient.getClientId());
        }

        count = oAuth2AuthedClientMapper.selectCount(Wrappers.<OAuth2AuthedClient>lambdaQuery().eq(OAuth2AuthedClient::getClientSecret, registeredClient.getClientSecret()));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("Registered client must be unique. " +
                                               "Found duplicate client secret for identifier: " + registeredClient.getId());
        }
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        OAuth2AuthedClient oAuth2AuthedClient = oAuth2AuthedClientMapper.selectById(id);
        return convert(oAuth2AuthedClient);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        OAuth2AuthedClient oAuth2AuthedClient = oAuth2AuthedClientMapper.selectOne(Wrappers.<OAuth2AuthedClient>lambdaQuery().eq(OAuth2AuthedClient::getClientId, clientId));
        return convert(oAuth2AuthedClient);
    }

    private RegisteredClient convert(OAuth2AuthedClient oAuth2AuthedClient) {
        if (ObjectUtils.isEmpty(oAuth2AuthedClient)) {
            return null;
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(oAuth2AuthedClient.getId())
                .clientId(oAuth2AuthedClient.getClientId())
                .clientSecret(oAuth2AuthedClient.getClientSecret())
                .clientName(oAuth2AuthedClient.getClientName())
                .clientAuthenticationMethods(clientAuthenticationMethods -> {
                    Set<ClientAuthenticationMethod> collect = Arrays.stream(StringUtils.commaDelimitedListToStringArray(oAuth2AuthedClient.getClientAuthenticationMethods())).map(ClientAuthenticationMethod::new).collect(Collectors.toSet());
                    clientAuthenticationMethods.addAll(collect);
                })
                .authorizationGrantTypes(authorizationGrantTypes -> {
                    Set<AuthorizationGrantType> collect = Arrays.stream(StringUtils.commaDelimitedListToStringArray(oAuth2AuthedClient.getAuthorizationGrantTypes())).map(AuthorizationGrantType::new).collect(Collectors.toSet());
                    authorizationGrantTypes.addAll(collect);
                })
                .redirectUris(redirectUris -> redirectUris.addAll(StringUtils.commaDelimitedListToSet(oAuth2AuthedClient.getRedirectUris())))
                .scopes(scopes -> scopes.addAll(StringUtils.commaDelimitedListToSet(oAuth2AuthedClient.getScopes())))
                .clientSettings(ClientSettings.withSettings(parseMap(oAuth2AuthedClient.getClientSettings())).build())
                .tokenSettings(TokenSettings.withSettings(parseMap(oAuth2AuthedClient.getTokenSettings())).build());

        if (ObjectUtils.isNotEmpty(oAuth2AuthedClient.getClientIdIssuedAt())) {
            builder.clientIdIssuedAt(oAuth2AuthedClient.getClientIdIssuedAt().atZone(ZoneId.systemDefault()).toInstant());
        }
        if (ObjectUtils.isNotEmpty(oAuth2AuthedClient.getClientSecretExpiresAt())) {
            builder.clientSecretExpiresAt(oAuth2AuthedClient.getClientSecretExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
        }
        return builder.build();
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
