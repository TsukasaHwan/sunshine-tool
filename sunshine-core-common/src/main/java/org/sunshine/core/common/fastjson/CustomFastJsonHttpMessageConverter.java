package org.sunshine.core.common.fastjson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * 重写FastJsonHttpMessageConverter适配OpenApi3 byte[]转json
 *
 * @author Teamo
 * @since 2023/4/2
 */
public class CustomFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();

        try {
            int contentLength;

            if (object instanceof byte[]) {
                byte[] bytes = (byte[]) object;
                contentLength = bytes.length;
                outputMessage.getBody().write(bytes, 0, bytes.length);
            } else if (object instanceof String && JSON.isValidObject((String) object)) {
                byte[] strBytes = ((String) object).getBytes(getFastJsonConfig().getCharset());
                contentLength = strBytes.length;
                outputMessage.getBody().write(strBytes, 0, strBytes.length);
            } else {
                contentLength = JSON.writeTo(
                        outputMessage.getBody(),
                        object, getFastJsonConfig().getDateFormat(),
                        getFastJsonConfig().getWriterFilters(),
                        getFastJsonConfig().getWriterFeatures()
                );
            }

            if (headers.getContentLength() < 0 && getFastJsonConfig().isWriteContentLength()) {
                headers.setContentLength(contentLength);
            }
        } catch (JSONException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new HttpMessageNotWritableException("I/O error while writing output message", ex);
        }
    }
}
