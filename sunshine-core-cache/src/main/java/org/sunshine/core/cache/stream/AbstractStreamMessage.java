package org.sunshine.core.cache.stream;

import com.alibaba.fastjson2.annotation.JSONField;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author Teamo
 * @since 2024/7/8
 */
public abstract class AbstractStreamMessage implements Serializable {

    /**
     * 流名称
     *
     * @return 流名称
     */
    @JSONField(serialize = false)
    public abstract String getStreamKey();

    /**
     * 流修剪配置
     *
     * @return 流修剪配置
     */
    @JSONField(serialize = false)
    public TrimConfig getTrimConfig() {
        return new TrimConfig();
    }

    /**
     * 死信配置
     *
     * @return 死信配置
     */
    @JSONField(serialize = false)
    public DeadLetterConfig getDeadLetterConfig() {
        return new DeadLetterConfig();
    }

    public static class TrimConfig {

        /**
         * 流修剪执行周期cron
         * 默认：每10分钟修剪一次
         */
        private String cron = "0 */10 * * * ?";

        /**
         * 保留数量
         * 默认：500
         */
        private long maxCount = 500L;

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public long getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(long maxCount) {
            this.maxCount = maxCount;
        }
    }

    public static class DeadLetterConfig {

        /**
         * 是否启用专用调度器
         * 默认：false
         */
        private boolean enableDedicatedScheduler = false;

        /**
         * 死信处理执行周期cron
         * 默认：每分钟30秒
         */
        private String cron = "30 * * * * ?";

        /**
         * 消息处理超时时间
         * 默认：5分钟
         */
        private Duration pendingProcessingTimeout = Duration.ofMinutes(5);

        public boolean isEnableDedicatedScheduler() {
            return enableDedicatedScheduler;
        }

        public void setEnableDedicatedScheduler(boolean enableDedicatedScheduler) {
            this.enableDedicatedScheduler = enableDedicatedScheduler;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public Duration getPendingProcessingTimeout() {
            return pendingProcessingTimeout;
        }

        public void setPendingProcessingTimeout(Duration pendingProcessingTimeout) {
            this.pendingProcessingTimeout = pendingProcessingTimeout;
        }
    }

}
