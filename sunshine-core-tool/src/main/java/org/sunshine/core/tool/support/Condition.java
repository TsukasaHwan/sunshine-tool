package org.sunshine.core.tool.support;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.sunshine.core.tool.api.request.Query;
import org.sunshine.core.tool.util.BeanCallBack;
import org.sunshine.core.tool.util.BeanUtils;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * 分页工具
 *
 * @author Teamo
 */
public class Condition {

    /**
     * 转化成mybatis-plus中的Page
     *
     * @param query 查询条件
     * @return IPage
     */
    public static <T> IPage<T> getPage(Query query) {
        checkQuery(query);
        Page<T> page = Page.of(query.getCurrent(), query.getSize());
        String ascs = query.getAscs();
        String descs = query.getDescs();
        if (StringUtils.isNotBlank(ascs)) {
            page.addOrder(OrderItem.ascs(getUnderlineColumns(StringUtils.delimitedListToStringArray(ascs, StringPool.COMMA))));
        }
        if (StringUtils.isNotBlank(descs)) {
            page.addOrder(OrderItem.descs(getUnderlineColumns(StringUtils.delimitedListToStringArray(descs, StringPool.COMMA))));
        }
        return page;
    }

    /**
     * 转化成spring-data-jpa中的Page
     *
     * @param query 查询条件
     * @return PageRequest
     */
    public static PageRequest getPageRequest(Query query) {
        checkQuery(query);
        PageRequest pageRequest = PageRequest.of(query.getCurrent(), query.getSize());
        String ascs = query.getAscs();
        String descs = query.getDescs();
        if (StringUtils.isNotBlank(ascs)) {
            pageRequest = pageRequest.withSort(Sort.Direction.ASC, getUnderlineColumns(StringUtils.delimitedListToStringArray(ascs, StringPool.COMMA)));
        }
        if (StringUtils.isNotBlank(descs)) {
            pageRequest = pageRequest.withSort(Sort.Direction.DESC, getUnderlineColumns(StringUtils.delimitedListToStringArray(descs, StringPool.COMMA)));
        }
        return pageRequest;
    }

    /**
     * 分页实体类集合包装
     *
     * @param page   源数据
     * @param target 目标数据
     * @param <E>    实体类
     * @param <V>    VO类
     * @return mybatis-plus分页
     */
    public static <E, V> IPage<V> pageVo(IPage<E> page, Supplier<V> target) {
        return pageVo(page, target, null);
    }

    /**
     * 分页实体类集合包装
     *
     * @param page     源数据
     * @param target   目标数据
     * @param callback 需要在拷贝中处理的逻辑函数
     * @param <E>      实体类
     * @param <V>      VO类
     * @return mybatis-plus分页
     */
    public static <E, V> IPage<V> pageVo(IPage<E> page, Supplier<V> target, BeanCallBack<E, V> callback) {
        List<E> records = page.getRecords();
        List<V> collect = BeanUtils.copyListProperties(records, target, callback);
        IPage<V> pageVo = Page.of(page.getCurrent(), page.getSize(), page.getTotal());
        pageVo.setRecords(collect);
        return pageVo;
    }

    /**
     * 检查query
     *
     * @param query Query
     */
    public static void checkQuery(Query query) {
        Integer current = query.getCurrent();
        Integer size = query.getSize();
        if (current == null || current <= 0) {
            query.setCurrent(Query.DEFAULT_CURRENT);
        }
        if (size == null || size <= 0) {
            query.setSize(Query.DEFAULT_SIZE);
        }
    }

    /**
     * 将传入驼峰排序字段转换为下划线格式
     *
     * @param columns 字段数组
     * @return 排序列表
     */
    private static String[] getUnderlineColumns(String[] columns) {
        return Arrays.stream(columns).map(column -> StringUtils.humpToUnderline(StringUtils.cleanIdentifier(column))).toArray(String[]::new);
    }
}
