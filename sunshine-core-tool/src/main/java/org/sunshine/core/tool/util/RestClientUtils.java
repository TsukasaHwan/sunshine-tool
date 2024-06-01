package org.sunshine.core.tool.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * {@link RestClient}工具类
 *
 * <p><strong>原因:</strong>由于RestTemplate从 5.0 开始，处于维护模式，Spring官方建议使用{@link RestClient}
 * ，它具有更现代的 API。
 *
 * @author Teamo
 * @since 2024/6/1
 */
public class RestClientUtils {

    /**
     * 使用内部类JVM机制保持单例对象
     */
    private static class RestClientInstance {
        private static final RestClient INSTANCE = SpringUtils.getBean(RestClient.class);
    }

    public static RestClient getRestClient() {
        return RestClientUtils.RestClientInstance.INSTANCE;
    }

    // ----------------------------------GET-------------------------------------------------------

    /**
     * GET请求调用方式
     *
     * @param url 请求URL
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec get(String url) {
        return executeRequest(HttpMethod.GET, url);
    }

    /**
     * GET请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec get(String url, Object... uriVariables) {
        return executeRequest(HttpMethod.GET, url, uriVariables);
    }

    /**
     * GET请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec get(String url, Map<String, ?> uriVariables) {
        return executeRequest(HttpMethod.GET, url, uriVariables);
    }

    /**
     * 带请求头的GET请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec get(String url, Map<String, String> headers, Object... uriVariables) {
        return executeRequestWithHeaders(HttpMethod.GET, url, headers, uriVariables);
    }

    /**
     * 带请求头的GET请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec get(String url, Map<String, String> headers, Map<String, ?> uriVariables) {
        return executeRequestWithHeaders(HttpMethod.GET, url, headers, uriVariables);
    }

    // ----------------------------------POST-------------------------------------------------------

    /**
     * POST请求调用方式
     *
     * @param url 请求URL
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec post(String url) {
        return executeRequest(HttpMethod.POST, url);
    }

    /**
     * POST请求调用方式
     *
     * @param url          请求URL
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec post(String url, Object bodyValue, Object... uriVariables) {
        return executeRequestWithBody(HttpMethod.POST, url, bodyValue, uriVariables);
    }

    /**
     * POST请求调用方式
     *
     * @param url          请求URL
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec post(String url, Object bodyValue, Map<String, ?> uriVariables) {
        return executeRequestWithBody(HttpMethod.POST, url, bodyValue, uriVariables);
    }

    /**
     * 带请求头的POST请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec post(String url, Map<String, String> headers, Object bodyValue, Object... uriVariables) {
        return executeRequestWithBodyAndHeaders(HttpMethod.POST, url, headers, bodyValue, uriVariables);
    }

    /**
     * 带请求头的POST请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec post(String url, Map<String, String> headers, Object bodyValue, Map<String, ?> uriVariables) {
        return executeRequestWithBodyAndHeaders(HttpMethod.POST, url, headers, bodyValue, uriVariables);
    }

    // ----------------------------------PUT-------------------------------------------------------

    /**
     * PUT请求调用方式
     *
     * @param url 请求URL
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec put(String url) {
        return executeRequest(HttpMethod.PUT, url);
    }

    /**
     * PUT请求调用方式
     *
     * @param url          请求URL
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec put(String url, Object bodyValue, Object... uriVariables) {
        return executeRequestWithBody(HttpMethod.PUT, url, bodyValue, uriVariables);
    }

    /**
     * PUT请求调用方式
     *
     * @param url          请求URL
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec put(String url, Object bodyValue, Map<String, ?> uriVariables) {
        return executeRequestWithBody(HttpMethod.PUT, url, bodyValue, uriVariables);
    }

    /**
     * 带请求头的PUT请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec put(String url, Map<String, String> headers, Object bodyValue, Object... uriVariables) {
        return executeRequestWithBodyAndHeaders(HttpMethod.PUT, url, headers, bodyValue, uriVariables);
    }

    /**
     * 带请求头的PUT请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param bodyValue    请求参数体
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec put(String url, Map<String, String> headers, Object bodyValue, Map<String, ?> uriVariables) {
        return executeRequestWithBodyAndHeaders(HttpMethod.PUT, url, headers, bodyValue, uriVariables);
    }

    // ----------------------------------DELETE-------------------------------------------------------

    /**
     * DELETE请求调用方式
     *
     * @param url 请求URL
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec delete(String url) {
        return executeRequest(HttpMethod.DELETE, url);
    }

    /**
     * DELETE请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec delete(String url, Object... uriVariables) {
        return executeRequest(HttpMethod.DELETE, url, uriVariables);
    }

    /**
     * DELETE请求调用方式
     *
     * @param url          请求URL
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec delete(String url, Map<String, ?> uriVariables) {
        return executeRequest(HttpMethod.DELETE, url, uriVariables);
    }

    /**
     * 带请求头的DELETE请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param uriVariables URI中的变量，按顺序依次对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec delete(String url, Map<String, String> headers, Object... uriVariables) {
        return executeRequestWithHeaders(HttpMethod.DELETE, url, headers, uriVariables);
    }

    /**
     * 带请求头的DELETE请求调用方式
     *
     * @param url          请求URL
     * @param headers      请求头参数
     * @param uriVariables URI中的变量，与Map中的key对应
     * @return RestClient.ResponseSpec 响应对象类
     */
    public static RestClient.ResponseSpec delete(String url, Map<String, String> headers, Map<String, ?> uriVariables) {
        return executeRequestWithHeaders(HttpMethod.DELETE, url, headers, uriVariables);
    }

    // ----------------------------------PRIVATE METHOD-------------------------------------------------------

    private static RestClient.ResponseSpec executeRequest(HttpMethod method, String url, Object... uriVariables) {
        return getRestClient()
                .method(method)
                .uri(url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static RestClient.ResponseSpec executeRequest(HttpMethod method, String url, Map<String, ?> uriVariables) {
        return getRestClient()
                .method(method)
                .uri(url, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static RestClient.ResponseSpec executeRequestWithBody(HttpMethod method, String url, Object bodyValue, Object... uriVariables) {
        return getRestClient()
                .method(method)
                .uri(url, uriVariables)
                .body(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static RestClient.ResponseSpec executeRequestWithBody(HttpMethod method, String url, Object bodyValue, Map<String, ?> uriVariables) {
        return getRestClient()
                .method(method)
                .uri(url, uriVariables)
                .body(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static RestClient.ResponseSpec executeRequestWithHeaders(HttpMethod method, String url, Map<String, String> headers, Object... uriVariables) {
        return getRestClient()
                .method(method)
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static RestClient.ResponseSpec executeRequestWithHeaders(HttpMethod method, String url, Map<String, String> headers, Map<String, ?> uriVariables) {
        return getRestClient()
                .method(method)
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static RestClient.ResponseSpec executeRequestWithBodyAndHeaders(HttpMethod method, String url, Map<String, String> headers, Object bodyValue, Object... uriVariables) {
        return getRestClient()
                .method(method)
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .body(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    private static RestClient.ResponseSpec executeRequestWithBodyAndHeaders(HttpMethod method, String url, Map<String, String> headers, Object bodyValue, Map<String, ?> uriVariables) {
        return getRestClient()
                .method(method)
                .uri(url, uriVariables)
                .headers(requestHeaders -> requestHeaders.setAll(headers))
                .body(bodyValue)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }
}
