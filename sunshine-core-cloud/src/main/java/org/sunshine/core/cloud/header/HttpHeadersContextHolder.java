/**
 * Copyright (c) 2018-2028, DreamLu 卢春梦 (qq596392912@gmail.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sunshine.core.cloud.header;

import org.springframework.core.NamedThreadLocal;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.sunshine.core.cloud.properties.FeignHeadersProperties;
import org.sunshine.core.tool.util.CollectionUtils;
import org.sunshine.core.tool.util.StringUtils;
import org.sunshine.core.tool.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 * HttpHeadersContext
 *
 * @author Teamo
 */
public class HttpHeadersContextHolder {
    private static final ThreadLocal<HttpHeaders> HTTP_HEADERS_HOLDER = new NamedThreadLocal<>("Feign HttpHeaders");

    static void set(HttpHeaders httpHeaders) {
        HTTP_HEADERS_HOLDER.set(httpHeaders);
    }

    @Nullable
    public static HttpHeaders get() {
        return HTTP_HEADERS_HOLDER.get();
    }

    static void remove() {
        HTTP_HEADERS_HOLDER.remove();
    }

    @Nullable
    public static HttpHeaders toHeaders(FeignHeadersProperties properties) {
        HttpServletRequest request = WebUtils.getRequest();
        if (request == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        List<String> allowHeadsList = properties.getAllowed();
        // 传递请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                // 只支持配置的 header
                if (CollectionUtils.isNotEmpty(allowHeadsList) && allowHeadsList.contains(key)) {
                    String values = request.getHeader(key);
                    // header value 不为空的 传递
                    if (StringUtils.isNotBlank(values)) {
                        headers.add(key, values);
                    }
                }
            }
        }
        return headers.isEmpty() ? null : headers;
    }
}
