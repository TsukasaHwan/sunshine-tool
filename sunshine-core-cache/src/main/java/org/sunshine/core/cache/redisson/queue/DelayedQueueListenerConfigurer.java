package org.sunshine.core.cache.redisson.queue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2024/3/7
 */
public class DelayedQueueListenerConfigurer implements InitializingBean, DisposableBean, ApplicationContextAware {

    private final List<DelayedQueueListener<?>> delayedQueueListenerList;
    private final RedissonClient redissonClient;
    private ApplicationContext context;
    private ThreadPoolExecutor delayedThreadPoolExecutor;

    public DelayedQueueListenerConfigurer(List<DelayedQueueListener<?>> delayedQueueListenerList, RedissonClient redissonClient) {
        this.delayedQueueListenerList = delayedQueueListenerList;
        this.redissonClient = redissonClient;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        delayedQueueListenerList.forEach(delayedQueueListener -> {
            DelayedQueuePollTask<?> delayedQueuePollTask = context.getBean(getDelayedQueuePollTaskBeanName(delayedQueueListener), DelayedQueuePollTask.class);
            delayedQueuePollTask.destroy();
        });
        if (delayedThreadPoolExecutor == null) {
            return;
        }
        delayedThreadPoolExecutor.shutdownNow();
        delayedThreadPoolExecutor = null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(delayedQueueListenerList, "delayedQueueListenerList must not be empty!");

        int numberOfJob = delayedQueueListenerList.stream().filter(DelayedQueueListener::isEnable).toList().size();
        if (numberOfJob == 0) {
            return;
        }
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("delayed-queue-thread-%d").build();
        delayedThreadPoolExecutor = new ThreadPoolExecutor(
                numberOfJob,
                numberOfJob,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                namedThreadFactory
        );
        GenericApplicationContext appContext = (GenericApplicationContext) context;
        delayedQueueListenerList.stream()
                .filter(DelayedQueueListener::isEnable)
                .forEach(delayedQueueListener -> {
                    String beanName = getDelayedQueuePollTaskBeanName(delayedQueueListener);
                    appContext.registerBean(beanName, DelayedQueuePollTask.class, redissonClient, delayedQueueListener);
                    DelayedQueuePollTask<?> delayedQueuePollTask = (DelayedQueuePollTask<?>) context.getBean(beanName);
                    delayedThreadPoolExecutor.execute(delayedQueuePollTask);
                });
    }

    private String getDelayedQueuePollTaskBeanName(DelayedQueueListener<?> delayedQueueListener) {
        return delayedQueueListener.getClass().getName() + "." + DelayedQueuePollTask.class.getSimpleName();
    }
}
