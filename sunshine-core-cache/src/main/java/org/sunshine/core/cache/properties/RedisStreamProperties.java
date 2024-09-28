package org.sunshine.core.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Teamo
 * @since 2024/9/29
 */
@ConfigurationProperties("spring.data.redis.stream")
public class RedisStreamProperties {

    /**
     * 批量处理消息的数量。
     * <p>默认为10。
     */
    private int batchSize = 10;

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
        private boolean enable = true;

        /**
         * 核心线程数。
         * <p>默认值为为核心处理器数量的两倍。
         * <p><b>可以在运行时修改此设置，例如通过JMX。</b>
         */
        private int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;

        /**
         * 最大线程数。
         * <p>默认值为核心线程数的四倍，并确保最小值为512。
         * <p><b>可以在运行时修改此设置，例如通过JMX。</b>
         */
        private int maxPoolSize = Math.max(corePoolSize * 4, 512);

        /**
         * 队列容量。
         * <p>默认值为500。
         * <p>任何正值都将导致LinkedBlockingQueue实例;
         * 任何其他值都将导致SynchronousQueue实例。
         */
        private int queueCapacity = 500;

        /**
         * 线程存活时间（单位：秒）
         * <p>默认值为30秒。
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

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
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
