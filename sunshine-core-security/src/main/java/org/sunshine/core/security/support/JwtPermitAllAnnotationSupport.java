package org.sunshine.core.security.support;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.sunshine.core.tool.util.ClassUtils;

import javax.annotation.security.PermitAll;

/**
 * @author Teamo
 * @since 2023/3/23
 */
public class JwtPermitAllAnnotationSupport extends AbstractJwtSecurityAnnotationSupport {

    @Override
    protected boolean hasAnnotation(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
        return ClassUtils.getAnnotation(handlerMethod, PermitAll.class) != null;
    }
}
