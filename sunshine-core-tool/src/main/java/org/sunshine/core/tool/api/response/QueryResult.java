package org.sunshine.core.tool.api.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.sunshine.core.tool.api.request.Query;

import java.util.Collections;
import java.util.List;

/**
 * 分页数据封装
 *
 * @author Teamo
 * @since 2019/7/10
 */
@Schema
public class QueryResult<T> implements Response {

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private List<T> records;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private long current;

    /**
     * 当前分页总页数
     */
    @Schema(description = "当前分页总页数")
    private long pages;

    /**
     * 每页显示条数
     */
    @Schema(description = "每页显示条数")
    private long size;

    /**
     * 当前满足条件总行数
     */
    @Schema(description = "当前满足条件总行数")
    private long total;

    private QueryResult(Query query) {
        this.records = Collections.emptyList();
        this.current = query.getCurrent();
        this.size = query.getSize();
        this.pages = 0L;
        this.total = 0L;
    }

    private QueryResult(IPage<T> page) {
        this.records = page.getRecords();
        this.current = page.getCurrent();
        this.pages = page.getPages();
        this.size = page.getSize();
        this.total = page.getTotal();
    }

    private QueryResult(Page<T> page) {
        this.records = page.getContent();
        this.current = page.getNumber();
        this.pages = page.getTotalPages();
        this.size = page.getSize();
        this.total = page.getNumberOfElements();
    }

    /**
     * 返回空的QueryResult
     *
     * @param query 分页条件
     * @param <T>   泛型
     * @return QueryResult
     */
    public static <T> QueryResult<T> newEmptyQueryResult(Query query) {
        return new QueryResult<>(query);
    }

    /**
     * 返回QueryResult
     *
     * @param page jpa分页接口
     * @param <T>  泛型
     * @return QueryResult
     */
    public static <T> QueryResult<T> newQueryResult(Page<T> page) {
        return new QueryResult<>(page);
    }

    /**
     * 返回QueryResult
     *
     * @param iPage mybatis-plus分页接口
     * @param <T>   泛型
     * @return QueryResult
     */
    public static <T> QueryResult<T> newQueryResult(IPage<T> iPage) {
        return new QueryResult<>(iPage);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
