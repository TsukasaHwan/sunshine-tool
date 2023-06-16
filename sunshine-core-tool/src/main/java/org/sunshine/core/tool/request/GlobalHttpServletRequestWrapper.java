package org.sunshine.core.tool.request;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.sunshine.core.tool.util.WebUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 全局Request包装
 *
 * @author Teamo
 */
public class GlobalHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 没被包装过的HttpServletRequest（特殊场景,需要自己过滤）
     */
    private final HttpServletRequest orgRequest;
    /**
     * 缓存报文,支持多次读取流
     */
    private byte[] body;


    public GlobalHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        orgRequest = request;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (super.getHeader(HttpHeaders.CONTENT_TYPE) == null) {
            return super.getInputStream();
        }

        if (super.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return super.getInputStream();
        }

        if (body == null) {
            body = WebUtils.getRequestBody(super.getInputStream()).getBytes();
        }

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    /**
     * 获取初始request
     *
     * @return HttpServletRequest
     */
    public HttpServletRequest getOrgRequest() {
        return orgRequest;
    }

    /**
     * 获取初始request
     *
     * @param request request
     * @return HttpServletRequest
     */
    public static HttpServletRequest getOrgRequest(HttpServletRequest request) {
        if (request instanceof GlobalHttpServletRequestWrapper) {
            return ((GlobalHttpServletRequestWrapper) request).getOrgRequest();
        }
        return request;
    }

}
