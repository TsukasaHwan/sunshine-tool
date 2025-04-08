package org.sunshine.security.jwt.authenticator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.sunshine.security.jwt.core.JwtTokenType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Teamo
 * @since 2025/4/7
 */
public class TokenAuthenticatorRegistry implements InitializingBean, ApplicationContextAware {

    private Map<JwtTokenType, AbstractTokenAuthenticator> authenticatorMap;

    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, AbstractTokenAuthenticator> beansOfType = context.getBeansOfType(AbstractTokenAuthenticator.class);
        if (beansOfType.isEmpty()) {
            throw new IllegalStateException("未找到任何Token认证器实现，请确认是否已正确配置AbstractTokenAuthenticator的子类");
        }
        this.authenticatorMap = new HashMap<>(16);
        beansOfType.values().forEach(authenticator -> {
            JwtTokenType type = authenticator.getTokenType();
            if (type == null) {
                throw new IllegalStateException(
                        String.format("认证器%s未正确实现getTokenType()方法，必须返回非空的JwtTokenType",
                                authenticator.getClass().getSimpleName())
                );
            }
            if (this.authenticatorMap.containsKey(type)) {
                throw new IllegalStateException(
                        String.format("发现重复的令牌类型认证器: [%s]，已存在认证器: %s，冲突认证器: %s",
                                type.getValue(),
                                authenticatorMap.get(type).getClass().getSimpleName(),
                                authenticator.getClass().getSimpleName())
                );
            }
            this.authenticatorMap.put(type, authenticator);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public AbstractTokenAuthenticator getTokenAuthenticator(JwtTokenType tokenType) {
        if (authenticatorMap == null || authenticatorMap.isEmpty()) {
            throw new IllegalStateException("令牌认证注册器尚未初始化完成，请确认是否已完成Spring上下文初始化");
        }

        if (tokenType == null) {
            throw new IllegalArgumentException("获取认证器时tokenType参数不能为null");
        }

        AbstractTokenAuthenticator authenticator = authenticatorMap.get(tokenType);
        if (authenticator == null) {
            throw new UnsupportedOperationException(
                    String.format("不支持处理该类型的令牌: %s，已注册类型: %s",
                            tokenType.getValue(),
                            String.join(", ", authenticatorMap.keySet().stream()
                                    .map(JwtTokenType::getValue)
                                    .toList()))
            );
        }
        return authenticatorMap.get(tokenType);
    }
}
