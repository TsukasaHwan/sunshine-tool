package org.sunshine.core.tool.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teamo
 * @since 2022/08/24
 */
public class ImportEventListener<T> extends AnalysisEventListener<T> {

    private int batchCount = 3000;

    private List<T> dataList = new ArrayList<>();

    private final ImportExcelService<T> importExcelService;

    public ImportEventListener(ImportExcelService<T> importExcelService) {
        this.importExcelService = importExcelService;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        dataList.add(data);
        if (dataList.size() >= batchCount) {
            importExcelService.handle(dataList, context);
            dataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!dataList.isEmpty()) {
            if (dataList.size() < batchCount) {
                importExcelService.handle(dataList, context);
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

    public ImportExcelService<T> getImportExcel() {
        return importExcelService;
    }
}
