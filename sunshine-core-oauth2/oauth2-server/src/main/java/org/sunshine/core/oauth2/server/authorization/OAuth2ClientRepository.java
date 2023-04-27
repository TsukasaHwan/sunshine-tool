package org.sunshine.core.oauth2.server.authorization;

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
import org.sunshine.core.oauth2.server.entity.OAuth2Client;
import org.sunshine.core.tool.util.BeanUtils;
import org.sunshine.core.tool.util.ObjectUtils;
import org.sunshine.core.tool.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Teamo
 * @since 2023/4/26
 */
public class OAuth2ClientRepository implements RegisteredClientRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BaseMapper<OAuth2Client> oAuth2ClientMapper;

    public OAuth2ClientRepository(BaseMapper<OAuth2Client> oAuth2ClientMapper) {
        Assert.notNull(oAuth2ClientMapper, "oAuth2ClientMapper cannot be null");
        this.oAuth2ClientMapper = oAuth2ClientMapper;

        ClassLoader classLoader = OAuth2ClientRepository.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        OAuth2Client oAuth2Client = oAuth2ClientMapper.selectById(registeredClient.getId());
        if (ObjectUtils.isEmpty(oAuth2Client)) {
            insertClient(registeredClient);
        } else {
            updateClient(registeredClient);
        }
    }

    private void updateClient(RegisteredClient registeredClient) {
        List<String> clientAuthenticationMethods = new ArrayList<>(registeredClient.getClientAuthenticationMethods().size());
        registeredClient.getClientAuthenticationMethods().forEach(clientAuthenticationMethod ->
                clientAuthenticationMethods.add(clientAuthenticationMethod.getValue()));

        List<String> authorizationGrantTypes = new ArrayList<>(registeredClient.getAuthorizationGrantTypes().size());
        registeredClient.getAuthorizationGrantTypes().forEach(authorizationGrantType ->
                authorizationGrantTypes.add(authorizationGrantType.getValue()));

        LambdaUpdateWrapper<OAuth2Client> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper
                .eq(OAuth2Client::getId, registeredClient.getId())
                .set(OAuth2Client::getClientName, registeredClient.getClientName())
                .set(OAuth2Client::getClientAuthenticationMethods, StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods))
                .set(OAuth2Client::getAuthorizationGrantTypes, StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes))
                .set(OAuth2Client::getRedirectUris, StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()))
                .set(OAuth2Client::getScopes, StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()))
                .set(OAuth2Client::getClientSettings, writeMap(registeredClient.getClientSettings().getSettings()))
                .set(OAuth2Client::getTokenSettings, writeMap(registeredClient.getTokenSettings().getSettings()));
        oAuth2ClientMapper.update(null, updateWrapper);
    }

    private void insertClient(RegisteredClient registeredClient) {
        assertUniqueIdentifiers(registeredClient);
        OAuth2Client oAuth2Client = new OAuth2Client();
        BeanUtils.copyProperties(registeredClient, oAuth2Client);
        if (ObjectUtils.isNotEmpty(registeredClient.getClientIdIssuedAt())) {
            oAuth2Client.setClientIdIssuedAt(LocalDateTime.ofInstant(registeredClient.getClientIdIssuedAt(), ZoneId.systemDefault()));
        }
        if (ObjectUtils.isNotEmpty(registeredClient.getClientSecretExpiresAt())) {
            oAuth2Client.setClientSecretExpiresAt(LocalDateTime.ofInstant(registeredClient.getClientSecretExpiresAt(), ZoneId.systemDefault()));
        }
        List<String> clientAuthenticationMethods = new ArrayList<>(registeredClient.getClientAuthenticationMethods().size());
        registeredClient.getClientAuthenticationMethods().forEach(clientAuthenticationMethod ->
                clientAuthenticationMethods.add(clientAuthenticationMethod.getValue()));

        List<String> authorizationGrantTypes = new ArrayList<>(registeredClient.getAuthorizationGrantTypes().size());
        registeredClient.getAuthorizationGrantTypes().forEach(authorizationGrantType ->
                authorizationGrantTypes.add(authorizationGrantType.getValue()));

        oAuth2Client.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        oAuth2Client.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
        oAuth2Client.setRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        oAuth2Client.setScopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));
        oAuth2Client.setClientSettings(writeMap(registeredClient.getClientSettings().getSettings()));
        oAuth2Client.setTokenSettings(writeMap(registeredClient.getTokenSettings().getSettings()));
        oAuth2ClientMapper.insert(oAuth2Client);
    }

    private void assertUniqueIdentifiers(RegisteredClient registeredClient) {
        Long count = oAuth2ClientMapper.selectCount(Wrappers.<OAuth2Client>lambdaQuery().eq(OAuth2Client::getClientId, registeredClient.getClientId()));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("Registered client must be unique. " +
                                               "Found duplicate client identifier: " + registeredClient.getClientId());
        }

        count = oAuth2ClientMapper.selectCount(Wrappers.<OAuth2Client>lambdaQuery().eq(OAuth2Client::getClientSecret, registeredClient.getClientSecret()));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("Registered client must be unique. " +
                                               "Found duplicate client secret for identifier: " + registeredClient.getId());
        }
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        OAuth2Client oAuth2Client = oAuth2ClientMapper.selectById(id);
        return convert(oAuth2Client);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        OAuth2Client oAuth2Client = oAuth2ClientMapper.selectOne(Wrappers.<OAuth2Client>lambdaQuery().eq(OAuth2Client::getClientId, clientId));
        return convert(oAuth2Client);
    }

    private RegisteredClient convert(OAuth2Client oAuth2Client) {
        if (ObjectUtils.isEmpty(oAuth2Client)) {
            return null;
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(oAuth2Client.getId())
                .clientId(oAuth2Client.getClientId())
                .clientSecret(oAuth2Client.getClientSecret())
                .clientName(oAuth2Client.getClientName())
                .clientAuthenticationMethods(clientAuthenticationMethods -> {
                    Set<ClientAuthenticationMethod> collect = Arrays.stream(StringUtils.commaDelimitedListToStringArray(oAuth2Client.getClientAuthenticationMethods())).map(ClientAuthenticationMethod::new).collect(Collectors.toSet());
                    clientAuthenticationMethods.addAll(collect);
                })
                .authorizationGrantTypes(authorizationGrantTypes -> {
                    Set<AuthorizationGrantType> collect = Arrays.stream(StringUtils.commaDelimitedListToStringArray(oAuth2Client.getAuthorizationGrantTypes())).map(AuthorizationGrantType::new).collect(Collectors.toSet());
                    authorizationGrantTypes.addAll(collect);
                })
                .redirectUris(redirectUris -> redirectUris.addAll(StringUtils.commaDelimitedListToSet(oAuth2Client.getRedirectUris())))
                .scopes(scopes -> scopes.addAll(StringUtils.commaDelimitedListToSet(oAuth2Client.getScopes())))
                .clientSettings(ClientSettings.withSettings(parseMap(oAuth2Client.getClientSettings())).build())
                .tokenSettings(TokenSettings.withSettings(parseMap(oAuth2Client.getTokenSettings())).build());

        if (ObjectUtils.isNotEmpty(oAuth2Client.getClientIdIssuedAt())) {
            builder.clientIdIssuedAt(oAuth2Client.getClientIdIssuedAt().atZone(ZoneId.systemDefault()).toInstant());
        }
        if (ObjectUtils.isNotEmpty(oAuth2Client.getClientSecretExpiresAt())) {
            builder.clientSecretExpiresAt(oAuth2Client.getClientSecretExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
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
