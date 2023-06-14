package org.sunshine.core.cloud.header;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.sunshine.core.cloud.properties.FeignHeadersProperties;

import java.util.concurrent.Callable;

/**
 * HttpHeaders hystrix Callable
 *
 * @param <V> 泛型标记
 * @author Teamo
 */
public class HttpHeadersCallable<V> implements Callable<V> {

    private final Callable<V> delegate;

    @Nullable
    private HttpHeaders httpHeaders;

    public HttpHeadersCallable(Callable<V> delegate, FeignHeadersProperties properties) {
        this.delegate = delegate;
        this.httpHeaders = HttpHeadersContextHolder.toHeaders(properties);
    }

    @Override
    public V call() throws Exception {
        if (httpHeaders == null) {
            return delegate.call();
        }
        try {
            HttpHeadersContextHolder.set(httpHeaders);
            return delegate.call();
        } finally {
            HttpHeadersContextHolder.remove();
            httpHeaders.clear();
            httpHeaders = null;
        }
    }
}
