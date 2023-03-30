package org.sunshine.core.log.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author Teamo
 * @since 2021/06/02
 */
public class OperateLogEvent extends ApplicationEvent {

    public OperateLogEvent(Map<String, Object> source) {
        super(source);
    }
}
