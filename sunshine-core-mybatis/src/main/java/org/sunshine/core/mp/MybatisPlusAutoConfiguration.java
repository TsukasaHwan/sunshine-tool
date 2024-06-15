package org.sunshine.core.mp;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusInnerInterceptorAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sunshine.core.mp.extension.injector.InsertBatchSqlInjector;
import org.sunshine.core.mp.extension.plugin.DynamicTableNameHandler;
import org.sunshine.core.mp.extension.plugin.DynamicTableSuffixContextHolder;
import org.sunshine.core.mp.handler.AutoFillMetaObjectHandler;

import java.util.List;

/**
 * @author Teamo
 * @since 2023/3/31
 */
@EnableTransactionManagement
@AutoConfiguration(before = MybatisPlusInnerInterceptorAutoConfiguration.class)
public class MybatisPlusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(List<InnerInterceptor> innerInterceptorList) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        boolean havePageInterceptor = innerInterceptorList.stream()
                .anyMatch(innerInterceptor -> innerInterceptor instanceof PaginationInnerInterceptor);
        if (!havePageInterceptor) {
            innerInterceptorList.add(new PaginationInnerInterceptor(DbType.MYSQL));
        }
        innerInterceptorList.forEach(interceptor::addInnerInterceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler() {
        return new AutoFillMetaObjectHandler();
    }

    @Bean
    @ConditionalOnMissingBean(InsertBatchSqlInjector.class)
    public InsertBatchSqlInjector insertBatchSqlInjector() {
        return new InsertBatchSqlInjector();
    }

    @Bean
    @ConditionalOnBean(DynamicTableNameHandler.class)
    @ConditionalOnMissingBean(DynamicTableNameInnerInterceptor.class)
    public DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor(DynamicTableNameHandler dynamicTableNameHandler) {
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler(dynamicTableNameHandler);
        dynamicTableNameInnerInterceptor.setHook(DynamicTableSuffixContextHolder::clear);
        return dynamicTableNameInnerInterceptor;
    }
}
