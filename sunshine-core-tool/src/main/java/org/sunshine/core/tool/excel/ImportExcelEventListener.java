package org.sunshine.core.tool.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel文件导入事件监听器。
 * 用于处理Excel数据的批量读取和处理。
 *
 * @param <T> 数据类型
 * @author Teamo
 * @since 2022/08/24
 */
public class ImportExcelEventListener<T> extends AnalysisEventListener<T> {

    /**
     * 批量处理的条数
     */
    private int batchCount;

    /**
     * 临时存储读取到的数据
     */
    private List<T> dataList = new ArrayList<>();

    /**
     * 数据处理接口
     */
    private final ImportExcelHandler<T> handler;

    /**
     * 构造函数。
     *
     * @param handler    数据处理接口实例
     * @param batchCount 每次处理的数据量
     */
    public ImportExcelEventListener(ImportExcelHandler<T> handler, int batchCount) {
        this.handler = handler;
        this.batchCount = batchCount;
    }

    /**
     * 当读取到数据时被调用。
     * 将数据添加到列表中，达到一定数量后处理。
     *
     * @param data    读取到的数据
     * @param context 分析上下文
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        dataList.add(data);
        if (dataList.size() >= batchCount) {
            handler.handle(dataList, context);
            // 清空已处理的数据
            dataList.clear();
        }
    }

    /**
     * 当所有数据都被读取并处理后调用。
     * 处理剩余的数据（如果有的话）。
     *
     * @param context 分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!dataList.isEmpty()) {
            if (dataList.size() < batchCount) {
                // 处理剩余数据
                handler.handle(dataList, context);
            }
            // 清空剩余数据
            dataList.clear();
        }
    }

    /**
     * 获取批量处理的条数。
     *
     * @return 批量处理的条数
     */
    public int getBatchCount() {
        return batchCount;
    }

    /**
     * 设置批量处理的条数。
     *
     * @param batchCount 批量处理的条数
     */
    public void setBatchCount(int batchCount) {
        this.batchCount = batchCount;
    }

    /**
     * 获取临时存储的数据列表。
     *
     * @return 数据列表
     */
    public List<T> getDataList() {
        return dataList;
    }

    /**
     * 设置临时存储的数据列表。
     *
     * @param dataList 数据列表
     */
    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}