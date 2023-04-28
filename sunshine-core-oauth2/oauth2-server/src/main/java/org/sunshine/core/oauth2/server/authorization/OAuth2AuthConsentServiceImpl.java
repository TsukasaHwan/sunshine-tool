package org.sunshine.core.oauth2.server.authorization;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.sunshine.core.oauth2.server.entity.OAuth2AuthConsent;
import org.sunshine.core.tool.util.BeanUtils;
import org.sunshine.core.tool.util.ObjectUtils;
import org.sunshine.core.tool.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Teamo
 * @since 2023/4/26
 */
public class OAuth2AuthConsentServiceImpl implements OAuth2AuthorizationConsentService {

    private final BaseMapper<OAuth2AuthConsent> oAuth2AuthConsentMapper;

    private final RegisteredClientRepository registeredClientRepository;

    public OAuth2AuthConsentServiceImpl(BaseMapper<OAuth2AuthConsent> oAuth2AuthConsentMapper, RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(oAuth2AuthConsentMapper, "oAuth2AuthConsentMapper cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.oAuth2AuthConsentMapper = oAuth2AuthConsentMapper;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        OAuth2AuthorizationConsent existingAuthorizationConsent = findById(
                authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
        if (ObjectUtils.isEmpty(existingAuthorizationConsent)) {
            insertAuthConsent(authorizationConsent);
        } else {
            updateAuthConsent(authorizationConsent);
        }
    }

    private void insertAuthConsent(OAuth2AuthorizationConsent authorizationConsent) {
        OAuth2AuthConsent oAuth2AuthConsent = new OAuth2AuthConsent();
        oAuth2AuthConsent.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        oAuth2AuthConsent.setPrincipalName(authorizationConsent.getPrincipalName());
        oAuth2AuthConsent.setAuthorities(StringUtils.collectionToCommaDelimitedString(authorizationConsent.getAuthorities()));
        oAuth2AuthConsentMapper.insert(oAuth2AuthConsent);
    }

    private void updateAuthConsent(OAuth2AuthorizationConsent authorizationConsent) {
        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        LambdaUpdateWrapper<OAuth2AuthConsent> wrapper = Wrappers.<OAuth2AuthConsent>lambdaUpdate()
                .eq(OAuth2AuthConsent::getRegisteredClientId, authorizationConsent.getRegisteredClientId())
                .eq(OAuth2AuthConsent::getPrincipalName, authorizationConsent.getPrincipalName())
                .set(OAuth2AuthConsent::getAuthorities, StringUtils.collectionToCommaDelimitedString(authorities));
        oAuth2AuthConsentMapper.update(null, wrapper);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        LambdaQueryWrapper<OAuth2AuthConsent> wrapper = Wrappers.<OAuth2AuthConsent>lambdaQuery()
                .eq(OAuth2AuthConsent::getRegisteredClientId, authorizationConsent.getRegisteredClientId())
                .eq(OAuth2AuthConsent::getPrincipalName, authorizationConsent.getPrincipalName());
        oAuth2AuthConsentMapper.delete(wrapper);
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        LambdaQueryWrapper<OAuth2AuthConsent> wrapper = Wrappers.<OAuth2AuthConsent>lambdaQuery()
                .eq(OAuth2AuthConsent::getRegisteredClientId, registeredClientId)
                .eq(OAuth2AuthConsent::getPrincipalName, principalName);
        OAuth2AuthConsent oAuth2AuthConsent = oAuth2AuthConsentMapper.selectOne(wrapper);
        return convert(oAuth2AuthConsent);
    }

    private OAuth2AuthorizationConsent convert(OAuth2AuthConsent oAuth2AuthConsent) {
        if (ObjectUtils.isEmpty(oAuth2AuthConsent)) {
            return null;
        }
        String registeredClientId = oAuth2AuthConsent.getRegisteredClientId();
        RegisteredClient registeredClient = this.registeredClientRepository.findById(registeredClientId);
        if (ObjectUtils.isEmpty(registeredClient)) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + registeredClientId + "' was not found in the RegisteredClientRepository.");
        }
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(oAuth2AuthConsent.getRegisteredClientId(), oAuth2AuthConsent.getPrincipalName());
        String authorizationConsentAuthorities = oAuth2AuthConsent.getAuthorities();
        if (ObjectUtils.isNotEmpty(authorizationConsentAuthorities)) {
            for (String authority : StringUtils.commaDelimitedListToSet(authorizationConsentAuthorities)) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }
        return builder.build();
    }
}
