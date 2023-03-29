package org.sunshine.core.common.config;

import com.sunshine.core.log.mdc.ThreadPoolExecutorMdcWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Teamo
 * @since 2020/4/27
 */
@EnableAsync
@AutoConfiguration
public class AsyncConfiguration implements AsyncConfigurer {

    private final static Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Override
    public Executor getAsyncExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        ThreadPoolTaskExecutor executor = new ThreadPoolExecutorMdcWrapper();
        //核心线程池数量
        executor.setCorePoolSize(corePoolSize);
        //最大线程数量
        executor.setMaxPoolSize(Math.max(corePoolSize * 4, 256));
        //线程池的队列容量
        executor.setQueueCapacity(200);
        //当线程超过corePoolSize，线程存活时间
        executor.setKeepAliveSeconds(10);
        //用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁
        executor.setAwaitTerminationSeconds(60);
        //线程名称的前缀
        executor.setThreadNamePrefix("async-executor-");
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return ((throwable, method, objects) -> log.error("方法名称:{}", method.getName(), throwable));
    }
}
