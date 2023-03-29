package com.sunshine.core.log.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author Teamo
 * @since 2021/06/02
 */
public class ApiLogEvent extends ApplicationEvent {

    public ApiLogEvent(Map<String, Object> source) {
        super(source);
    }
}
