package org.sunshine.core.cache.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.sunshine.core.cache.stream.AbstractStreamListener;
import org.sunshine.core.cache.stream.StreamDeadLetterQueueProcessor;

import java.util.List;

/**
 * @author Teamo
 * @since 2025/3/13
 */
@EnableScheduling
@AutoConfiguration
@ConditionalOnBean(StreamDeadLetterQueueProcessor.class)
public class StreamTaskSchedulerConfiguration implements SchedulingConfigurer {

    private final List<AbstractStreamListener<?>> listeners;
    private final StreamDeadLetterQueueProcessor streamDeadLetterQueueProcessor;

    public StreamTaskSchedulerConfiguration(List<AbstractStreamListener<?>> listeners,
                                            StreamDeadLetterQueueProcessor streamDeadLetterQueueProcessor) {
        this.listeners = listeners;
        this.streamDeadLetterQueueProcessor = streamDeadLetterQueueProcessor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        for (AbstractStreamListener<?> listener : listeners) {
            if (listener.getDeadLetterConfig().isEnableDedicatedScheduler()) {
                taskRegistrar.addCronTask(() -> streamDeadLetterQueueProcessor.scheduleDeadLetterTask(listener), listener.getDeadLetterConfig().getCron());
            }
            taskRegistrar.addCronTask(() -> streamDeadLetterQueueProcessor.scheduleTrimTask(listener), listener.getTrimConfig().getCron());
        }
    }
}
