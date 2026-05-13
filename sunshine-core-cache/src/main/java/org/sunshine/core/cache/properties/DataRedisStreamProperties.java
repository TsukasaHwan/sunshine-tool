package org.sunshine.core.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Teamo
 * @since 2024/9/29
 */
@ConfigurationProperties("spring.data.redis.stream")
public class DataRedisStreamProperties {

    /**
     * 批量处理消息的数量。
     * <p>默认为10。
     */
    private int batchSize = 10;

    /**
     * 死信队列任务执行cron表达式。
     * <p>默认为30秒执行一次
     */
    private String deadLetterTaskCron;

    /**
     * 线程池配置。
     */
    private ThreadPool threadPool = new ThreadPool();

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getDeadLetterTaskCron() {
        return deadLetterTaskCron;
    }

    public void setDeadLetterTaskCron(String deadLetterTaskCron) {
        this.deadLetterTaskCron = deadLetterTaskCron;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public static class ThreadPool {

        /**
         * 是否启用自定义线程池。
         * <p>默认为true。
         */
        private Boolean enabled = true;

        /**
         * 核心线程数。
         * <p>默认值为为核心处理器的数量。
         * <p><b>可以在运行时修改此设置，例如通过JMX。</b>
         */
        private int corePoolSize = Runtime.getRuntime().availableProcessors();

        /**
         * 最大线程数。
         * <p>默认值为核心线程数的两倍。
         * <p><b>可以在运行时修改此设置，例如通过JMX。</b>
         */
        private int maxPoolSize = corePoolSize * 2;

        /**
         * 队列容量。
         * <p>默认值为500。
         * <p>任何正值都将导致LinkedBlockingQueue实例;
         * 任何其他值都将导致SynchronousQueue实例。
         * <p><b>短任务且高频率：</b>
         * <p>任务队列大小：500 ~ 1000
         * <p>说明：短任务通常执行时间很短，高频率的任务队列可以容纳更多的任务，以应对突发负载。
         * <p><b>长任务或低频率：</b>
         * <p>任务队列大小：100 ~ 500
         * <p>说明：长任务执行时间较长，队列不宜过大，以免占用过多内存；低频率任务队列也不需要太大。
         */
        private int queueCapacity = 500;

        /**
         * 线程存活时间（单位：秒）
         * <p>默认值为60秒。
         * <p><b>可以在运行时修改此设置，例如通过JMX。</b>
         */
        private int keepAliveSeconds = 60;

        /**
         * 是否在关机时等待计划任务完成，不中断正在运行的任务和执行队列中的所有任务。
         * <p>默认值为true。
         */
        private Boolean waitForJobsToCompleteOnShutdown = true;

        /**
         * 线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁。（单位：秒）
         * <p>默认值为120秒。
         */
        private int awaitTerminationSeconds = 120;

        /**
         * 线程名称的前缀。
         * <p>默认值为：redis-stream-thread-
         */
        private String threadNamePrefix = "redis-stream-thread-";

        /**
         * 拒绝策略。
         * <p>默认值为CallerRunsPolicy。
         *
         * @see RejectedExecutionHandler
         */
        private Class<? extends RejectedExecutionHandler> rejectedExecutionHandler = ThreadPoolExecutor.CallerRunsPolicy.class;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public int getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public Boolean getWaitForJobsToCompleteOnShutdown() {
            return waitForJobsToCompleteOnShutdown;
        }

        public void setWaitForJobsToCompleteOnShutdown(Boolean waitForJobsToCompleteOnShutdown) {
            this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
        }

        public int getAwaitTerminationSeconds() {
            return awaitTerminationSeconds;
        }

        public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
            this.awaitTerminationSeconds = awaitTerminationSeconds;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        public Class<? extends RejectedExecutionHandler> getRejectedExecutionHandler() {
            return rejectedExecutionHandler;
        }

        public void setRejectedExecutionHandler(Class<? extends RejectedExecutionHandler> rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
        }
    }
}
