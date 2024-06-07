package org.sunshine.core.tool.api.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.sunshine.core.tool.api.request.PageReqDto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页数据封装
 *
 * @author Teamo
 * @since 2019/7/10
 */
@Schema
public class PageResult<T> implements Serializable {

    /**
     * 分页数据
     */
    @Schema(description = "分页数据")
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

    private PageResult() {
    }

    private PageResult(PageReqDto pageReqDto) {
        this.records = Collections.emptyList();
        this.current = pageReqDto.getCurrent();
        this.size = pageReqDto.getSize();
        this.pages = 0L;
        this.total = 0L;
    }

    private PageResult(IPage<T> page) {
        this.records = page.getRecords();
        this.current = page.getCurrent();
        this.pages = page.getPages();
        this.size = page.getSize();
        this.total = page.getTotal();
    }

    private PageResult(Page<T> page) {
        this.records = page.getContent();
        this.current = page.getNumber();
        this.pages = page.getTotalPages();
        this.size = page.getSize();
        this.total = page.getNumberOfElements();
    }

    /**
     * 返回空的PageResult
     *
     * @param pageReqDto 分页条件
     * @param <T>        泛型
     * @return PageResult
     */
    public static <T> PageResult<T> newEmptyPageResult(PageReqDto pageReqDto) {
        return new PageResult<>(pageReqDto);
    }

    /**
     * 返回PageResult
     *
     * @param page jpa分页接口
     * @param <T>  泛型
     * @return PageResult
     */
    public static <T> PageResult<T> newPageResult(Page<T> page) {
        return new PageResult<>(page);
    }

    /**
     * 返回PageResult
     *
     * @param iPage mybatis-plus分页接口
     * @param <T>   泛型
     * @return PageResult
     */
    public static <T> PageResult<T> newPageResult(IPage<T> iPage) {
        return new PageResult<>(iPage);
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
