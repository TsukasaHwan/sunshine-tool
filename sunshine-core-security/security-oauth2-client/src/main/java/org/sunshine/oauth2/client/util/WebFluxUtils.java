package org.sunshine.oauth2.client.util;

import com.alibaba.fastjson2.JSON;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.sunshine.core.tool.api.response.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author Teamo
 * @since 2023/5/30
 */
public class WebFluxUtils {

    /**
     * WebFlux响应json信息
     *
     * @param resp   ServerHttpResponse
     * @param result Result<Void>
     * @return Mono<Void>
     */
    public static Mono<Void> renderJson(ServerHttpResponse resp, Result<?> result) {
        resp.setStatusCode(HttpStatus.OK);
        resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String responseResult = JSON.toJSONString(result);
        DataBuffer buffer = resp.bufferFactory().wrap(responseResult.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }
}
