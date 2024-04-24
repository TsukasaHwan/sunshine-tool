package org.sunshine.core.tool.util;

import org.springframework.http.HttpMethod;
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
        return executeRequest(HttpMethod.GET, url);
    }

    /**
     * GET请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec get(String url, Object... uriVariables) {
        return executeRequest(HttpMethod.GET, url, uriVariables);
    }

    /**
     * GET请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec get(String url, Map<String, ?> uriVariables) {
        return executeRequest(HttpMethod.GET, url, uriVariables);
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
        return executeRequestWithHeaders(HttpMethod.GET, url, headers, uriVariables);
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
        return executeRequestWithHeaders(HttpMethod.GET, url, headers, uriVariables);
    }

    // ----------------------------------POST-------------------------------------------------------

    /**
     * POST请求调用方式
     *
     * @param url 请求URL
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec post(String url) {
        return executeRequest(HttpMethod.POST, url);
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
        return executeRequestWithBody(HttpMethod.POST, url, bodyValue, uriVariables);
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
        return executeRequestWithBody(HttpMethod.POST, url, bodyValue, uriVariables);
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
        return executeRequestWithBodyAndHeaders(HttpMethod.POST, url, headers, bodyValue, uriVariables);
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
        return executeRequestWithBodyAndHeaders(HttpMethod.POST, url, headers, bodyValue, uriVariables);
    }

    // ----------------------------------PUT-------------------------------------------------------

    /**
     * PUT请求调用方式
     *
     * @param url 请求URL
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec put(String url) {
        return executeRequest(HttpMethod.PUT, url);
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
        return executeRequestWithBody(HttpMethod.PUT, url, bodyValue, uriVariables);
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
        return executeRequestWithBody(HttpMethod.PUT, url, bodyValue, uriVariables);
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
        return executeRequestWithBodyAndHeaders(HttpMethod.PUT, url, headers, bodyValue, uriVariables);
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
        return executeRequestWithBodyAndHeaders(HttpMethod.PUT, url, headers, bodyValue, uriVariables);
    }

    // ----------------------------------DELETE-------------------------------------------------------

    /**
     * DELETE请求调用方式
     *
     * @param url 请求URL
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec delete(String url) {
        return executeRequest(HttpMethod.DELETE, url);
    }

    /**
     * DELETE请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec delete(String url, Object... uriVariables) {
        return executeRequest(HttpMethod.DELETE, url, uriVariables);
    }

    /**
     * DELETE请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return WebClient.ResponseSpec 响应对象类
     */
    public static WebClient.ResponseSpec delete(String url, Map<String, ?> uriVariables) {
        return executeRequest(HttpMethod.DELETE, url, uriVariables);
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
        return executeRequestWithHeaders(HttpMethod.DELETE, url, headers, uriVariables);
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
        return executeRequestWithHeaders(HttpMethod.DELETE, url, headers, uriVariables);
    }

    // ----------------------------------PRIVATE METHOD-------------------------------------------------------

    private static WebClient.ResponseSpec executeRequest(HttpMethod method, String url, Object... uriVariables) {
        return getWebClient()
                .method(method)
                .uri(url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static WebClient.ResponseSpec executeRequestWithBody(HttpMethod method, String url, Object bodyValue, Object... uriVariables) {
        return getWebClient()
                .method(method)
                .uri(url, uriVariables)
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static WebClient.ResponseSpec executeRequestWithHeaders(HttpMethod method, String url, Map<String, String> headers, Object... uriVariables) {
        return getWebClient()
                .method(method)
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static WebClient.ResponseSpec executeRequestWithBodyAndHeaders(HttpMethod method, String url, Map<String, String> headers, Object bodyValue, Object... uriVariables) {
        return getWebClient()
                .method(method)
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .bodyValue(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }
}
