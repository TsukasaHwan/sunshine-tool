package org.sunshine.core.tool.excel;

import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.builder.ExcelReaderBuilder;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.sunshine.core.tool.util.Exceptions;
import org.sunshine.core.tool.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Teamo
 * @since 2022/08/24
 */
@FunctionalInterface
public interface ImportExcelHandler<T> {

    /**
     * 通过输入流读取Excel数据（带缓冲区处理）
     *
     * @param clazz       Excel行数据映射的目标类
     * @param batchCount  批量处理阈值，达到该数量时触发handle方法
     * @param inputStream 输入流（自动包装为缓冲流）
     * @return Excel读取构建器，用于后续解析操作
     * @throws IllegalArgumentException 当输入流为null时抛出
     */
    default ExcelReaderBuilder readExcel(Class<T> clazz, int batchCount, InputStream inputStream) {
        Assert.notNull(inputStream, "InputStream must not be null");
        ImportExcelEventListener<T> listener = new ImportExcelEventListener<>(this, batchCount);
        InputStream bis = inputStream instanceof BufferedInputStream ? inputStream : new BufferedInputStream(inputStream);
        return FesodSheet.read(bis, clazz, listener);
    }

    /**
     * 通过MultipartFile读取Excel数据（自动处理输入流）
     *
     * @param clazz         Excel行数据映射的目标类
     * @param batchCount    批量处理阈值
     * @param multipartFile Spring文件上传对象
     * @return Excel读取构建器
     * @throws IllegalArgumentException 当文件为null时抛出
     * @throws RuntimeException         IO异常时抛出包装异常
     */
    default ExcelReaderBuilder readExcel(Class<T> clazz, int batchCount, MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "MultipartFile must not be null");
        try {
            InputStream is = multipartFile.getInputStream();
            return readExcel(clazz, batchCount, is);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 通过文件对象读取Excel数据
     *
     * @param clazz      Excel行数据映射的目标类
     * @param batchCount 批量处理阈值
     * @param file       需要导入的Excel文件
     * @return Excel读取构建器
     * @throws IllegalArgumentException 当文件为null时抛出
     */
    default ExcelReaderBuilder readExcel(Class<T> clazz, int batchCount, File file) {
        Assert.notNull(file, "File must not be null");
        InputStream inputStream = FileUtils.getInputStream(file);
        return readExcel(clazz, batchCount, inputStream);
    }

    /**
     * 通过输入流读取Excel数据（每1000条数据执行{@link #handle(List, AnalysisContext)}）
     *
     * @param clazz       Excel行数据映射的目标类
     * @param inputStream 输入流（自动包装为缓冲流）
     */
    default ExcelReaderBuilder readExcel(Class<T> clazz, InputStream inputStream) {
        return readExcel(clazz, 1000, inputStream);
    }

    /**
     * 通过MultipartFile读取Excel数据（每1000条数据执行{@link #handle(List, AnalysisContext)}）
     *
     * @param clazz         Excel行数据映射的目标类
     * @param multipartFile Spring文件上传对象
     */
    default ExcelReaderBuilder readExcel(Class<T> clazz, MultipartFile multipartFile) {
        return readExcel(clazz, 1000, multipartFile);
    }

    /**
     * 通过文件对象读取Excel数据（每1000条数据执行{@link #handle(List, AnalysisContext)}）
     *
     * @param clazz Excel行数据映射的目标类
     * @param file  需要导入的Excel文件
     */
    default ExcelReaderBuilder readExcel(Class<T> clazz, File file) {
        return readExcel(clazz, 1000, file);
    }

    /**
     * 批量数据处理核心方法（需实现）
     *
     * @param dataList 当前批次的数据集合（最大数量为batchCount）
     * @param context  Excel解析上下文，包含当前工作表/行号等信息
     */
    void handle(List<T> dataList, AnalysisContext context);
}
