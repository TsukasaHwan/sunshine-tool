package org.sunshine.core.tool.util;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * 响应式网络请求客户端{@link WebClient}工具类
 *
 * <p><strong>原因:</strong>由于RestTemplate从 5.0 开始，处于维护模式，Spring官方建议使用{@link WebClient}
 * ，它具有更现代的 API，并支持同步、异步和流场景。
 *
 * @author Teamo
 * @since 2024/1/15
 */
public class WebClientUtils {

    /**
     * 使用内部类JVM机制保持单例对象
     */
    private static class WebClientInstance {
        private static final WebClient INSTANCE = SpringUtils.getBean(WebClient.class);
    }

    public static WebClient getWebClient() {
        return WebClientUtils.WebClientInstance.INSTANCE;
    }

    // ----------------------------------GET-------------------------------------------------------

    /**
     * GET请求调用方式
     *
     * @param url 请求URL
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec get(String url) {
        return getWebClient()
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * GET请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec get(String url, Object... uriVariables) {
        return getWebClient()
                .get()
                .uri(url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * GET请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec get(String url, Map<String, ?> uriVariables) {
        return getWebClient()
                .get()
                .uri(url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * 带请求头的GET请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec get(String url, Map<String, String> headers, Object... uriVariables) {
        return getWebClient()
                .get()
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * 带请求头的GET请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec get(String url, Map<String, String> headers, Map<String, ?> uriVariables) {
        return getWebClient()
                .get()
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    // ----------------------------------POST-------------------------------------------------------

    /**
     * POST请求调用方式
     *
     * @param url 请求URL
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec post(String url) {
        return getWebClient()
                .post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * POST请求调用方式
     *
     * @param url          请求URL
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec post(String url, Object bodyValue, Object... uriVariables) {
        return getWebClient()
                .post()
                .uri(url, uriVariables)
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * POST请求调用方式
     *
     * @param url          请求URL
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec post(String url, Object bodyValue, Map<String, ?> uriVariables) {
        return getWebClient()
                .post()
                .uri(url, uriVariables)
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * 带请求头的POST请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec post(String url, Map<String, String> headers, Object bodyValue, Object... uriVariables) {
        return getWebClient()
                .post()
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * 带请求头的POST请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec post(String url, Map<String, String> headers, Object bodyValue, Map<String, ?> uriVariables) {
        return getWebClient()
                .post()
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    // ----------------------------------PUT-------------------------------------------------------

    /**
     * PUT请求调用方式
     *
     * @param url 请求URL
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec put(String url) {
        return getWebClient()
                .put()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * PUT请求调用方式
     *
     * @param url          请求URL
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec put(String url, Object bodyValue, Object... uriVariables) {
        return getWebClient()
                .put()
                .uri(url, uriVariables)
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * PUT请求调用方式
     *
     * @param url          请求URL
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec put(String url, Object bodyValue, Map<String, ?> uriVariables) {
        return getWebClient()
                .put()
                .uri(url, uriVariables)
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * 带请求头的PUT请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec put(String url, Map<String, String> headers, Object bodyValue, Object... uriVariables) {
        return getWebClient()
                .put()
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * 带请求头的PUT请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec put(String url, Map<String, String> headers, Object bodyValue, Map<String, ?> uriVariables) {
        return getWebClient()
                .put()
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    // ----------------------------------DELETE-------------------------------------------------------

    /**
     * DELETE请求调用方式
     *
     * @param url 请求URL
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec delete(String url) {
        return getWebClient()
                .delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * DELETE请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec delete(String url, Object... uriVariables) {
        return getWebClient()
                .delete()
                .uri(url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * DELETE请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec delete(String url, Map<String, ?> uriVariables) {
        return getWebClient()
                .delete()
                .uri(url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * 带请求头的DELETE请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec delete(String url, Map<String, String> headers, Object... uriVariables) {
        return getWebClient()
                .delete()
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    /**
     * 带请求头的DELETE请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec delete(String url, Map<String, String> headers, Map<String, ?> uriVariables) {
        return getWebClient()
                .delete()
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }
}
