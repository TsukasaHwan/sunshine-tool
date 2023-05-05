package org.sunshine.core.tool.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.sunshine.core.tool.util.SpringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Teamo
 * @since 2022/08/24
 */
@FunctionalInterface
public interface ImportExcelService<T> {

    /**
     * 读取excel
     *
     * @param service 服务类class
     * @param clazz   excel class
     * @param files   导入的文件
     * @throws IOException IOException
     */
    default void doRead(Class<? extends ImportExcelService<T>> service, Class<T> clazz, MultipartFile... files) throws IOException {
        Assert.notNull(files, "MultipartFile must not be null");
        ImportExcelService<T> bean = SpringUtils.getBean(service);
        ImportEventListener<T> importListener;
        InputStream inputStream;
        for (MultipartFile file : files) {
            importListener = new ImportEventListener<>(bean);
            inputStream = new BufferedInputStream(file.getInputStream());
            ExcelReaderBuilder builder = EasyExcel.read(inputStream, clazz, importListener);
            builder.doReadAll();
        }
    }

    /**
     * 数据处理逻辑
     *
     * @param dataList 数据
     * @param context  {@link AnalysisContext}
     */
    void handle(List<T> dataList, AnalysisContext context);
}
