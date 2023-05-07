package org.sunshine.core.mp;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sunshine.core.mp.extension.injector.InsertBatchSqlInjector;
import org.sunshine.core.mp.extension.plugin.DynamicTableNameHandler;
import org.sunshine.core.mp.extension.plugin.DynamicTableSuffixContextHolder;
import org.sunshine.core.mp.handler.AutoFillMetaObjectHandler;

/**
 * @author Teamo
 * @since 2023/3/31
 */
@AutoConfiguration
@EnableTransactionManagement
public class MybatisPlusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(@Autowired(required = false) DynamicTableNameHandler dynamicTableNameHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (dynamicTableNameHandler != null) {
            DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
            dynamicTableNameInnerInterceptor.setTableNameHandler(dynamicTableNameHandler);
            dynamicTableNameInnerInterceptor.setHook(DynamicTableSuffixContextHolder::clear);
            interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        }
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
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
}
