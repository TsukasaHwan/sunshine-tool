package org.sunshine.core.tool.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teamo
 * @since 2022/08/24
 */
public class ImportExcelEventListener<T> extends AnalysisEventListener<T> {

    private int batchCount;

    private List<T> dataList = new ArrayList<>();

    private final ImportExcelHandler<T> handler;

    public ImportExcelEventListener(ImportExcelHandler<T> handler, int batchCount) {
        this.handler = handler;
        this.batchCount = batchCount;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        dataList.add(data);
        if (dataList.size() >= batchCount) {
            handler.handle(dataList, context);
            dataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!dataList.isEmpty()) {
            if (dataList.size() < batchCount) {
                handler.handle(dataList, context);
            }
            // 存储完成清理list
            dataList.clear();
        }
    }

    public int getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(int batchCount) {
        this.batchCount = batchCount;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
