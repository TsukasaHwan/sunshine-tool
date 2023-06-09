package org.sunshine.oauth2.client.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import org.sunshine.core.tool.api.response.Result;
import org.sunshine.oauth2.client.util.WebFluxUtils;
import reactor.core.publisher.Mono;

/**
 * @author Teamo
 * @since 2023/6/9
 */
public class WebFluxAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return WebFluxUtils.renderJson(exchange.getResponse(), Result.fail("拒绝访问"));
    }
}
