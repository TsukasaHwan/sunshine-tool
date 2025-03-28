package org.sunshine.core.cache.redisson.queue;

import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2025/3/27
 */
public class DelayedRecord<T> {

    /**
     * 队列名称
     */
    private final String queue;

    /**
     * 延迟消息
     */
    private final T value;

    /**
     * 延迟时间
     */
    private final long delay;

    /**
     * 时间单位
     */
    private final TimeUnit unit;

    private DelayedRecord(DelayedRecordBuilder<T> builder) {
        this.queue = builder.queue;
        this.value = builder.value;
        this.delay = builder.delay;
        this.unit = builder.unit;
    }

    public static <T> DelayedRecordBuilder<T> of(String queue, T value) {
        Assert.hasText(queue, "Queue must not be empty");
        Assert.notNull(value, "Value must be not null");
        return new DelayedRecordBuilder<>(queue, value);
    }

    public String getQueue() {
        return queue;
    }

    public T getValue() {
        return value;
    }

    public long getDelay() {
        return delay;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public static class DelayedRecordBuilder<T> {

        private final String queue;

        private final T value;

        private long delay;

        private TimeUnit unit;

        private DelayedRecordBuilder(String queue, T value) {
            this.queue = queue;
            this.value = value;
        }

        /**
         * 设置延迟时间
         */
        public DelayedRecordBuilder<T> withDelay(long delay, TimeUnit unit) {
            Assert.isTrue(delay >= 0L, "Delay must be greater than or equal to zero");
            Assert.notNull(unit, "TimeUnit must be not null");
            this.delay = delay;
            this.unit = unit;
            return this;
        }

        /**
         * 创建延迟消息
         */
        public DelayedRecord<T> create() {
            Assert.isTrue(this.delay >= 0L, "Delay must be greater than or equal to zero");
            Assert.notNull(this.unit, "TimeUnit must be not null");
            return new DelayedRecord<>(this);
        }
    }
}
