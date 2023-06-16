package org.sunshine.security.core.support;

import jakarta.annotation.security.PermitAll;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.sunshine.core.tool.util.ClassUtils;

/**
 * @author Teamo
 * @since 2023/3/23
 */
public class PermitAllAnnotationSupport extends AbstractSecurityAnnotationSupport {

    @Override
    protected boolean hasAnnotation(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
        return ClassUtils.getAnnotation(handlerMethod, PermitAll.class) != null;
    }
}
