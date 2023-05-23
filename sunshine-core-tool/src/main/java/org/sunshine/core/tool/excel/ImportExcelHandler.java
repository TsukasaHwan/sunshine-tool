package org.sunshine.core.tool.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.sunshine.core.tool.support.Try;
import org.sunshine.core.tool.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Teamo
 * @since 2022/08/24
 */
@FunctionalInterface
public interface ImportExcelHandler<T> {

    /**
     * 读取excel
     *
     * @param clazz      excel class
     * @param batchCount 插入数量
     * @param inputs     流
     */
    default void doConvert(Class<T> clazz, int batchCount, InputStream... inputs) {
        Assert.notNull(inputs, "InputStream must not be null");
        ImportEventListener<T> importListener;
        InputStream inputStream;
        for (InputStream is : inputs) {
            inputStream = is instanceof BufferedInputStream ? is : new BufferedInputStream(is);
            importListener = new ImportEventListener<>(this, batchCount);
            EasyExcel.read(inputStream, clazz, importListener).doReadAll();
        }
    }

    /**
     * 读取excel
     *
     * @param clazz      excel class
     * @param batchCount 插入数量
     * @param files      导入的文件
     */
    default void doConvert(Class<T> clazz, int batchCount, MultipartFile... files) {
        Assert.notNull(files, "InputStream must not be null");
        InputStream[] inputs = Arrays.stream(files).map(Try.apply(MultipartFile::getInputStream)).toArray(InputStream[]::new);
        doConvert(clazz, batchCount, inputs);
    }

    /**
     * 读取excel
     *
     * @param clazz      excel class
     * @param batchCount 插入数量
     * @param files      导入的文件
     */
    default void doConvert(Class<T> clazz, int batchCount, File... files) {
        Assert.notNull(files, "InputStream must not be null");
        InputStream[] inputs = Arrays.stream(files).map(FileUtils::getInputStream).toArray(InputStream[]::new);
        doConvert(clazz, batchCount, inputs);
    }

    /**
     * 读取excel
     *
     * @param clazz  excel class
     * @param inputs 流
     */
    default void doConvert(Class<T> clazz, InputStream... inputs) {
        doConvert(clazz, 3000, inputs);
    }

    /**
     * 读取excel
     *
     * @param clazz excel class
     * @param files 导入的文件
     */
    default void doConvert(Class<T> clazz, MultipartFile... files) {
        doConvert(clazz, 3000, files);
    }

    /**
     * 读取excel
     *
     * @param clazz excel class
     * @param files 导入的文件
     */
    default void doConvert(Class<T> clazz, File... files) {
        doConvert(clazz, 3000, files);
    }

    /**
     * 数据处理逻辑
     *
     * @param dataList 数据
     * @param context  {@link AnalysisContext}
     */
    void handle(List<T> dataList, AnalysisContext context);
}
