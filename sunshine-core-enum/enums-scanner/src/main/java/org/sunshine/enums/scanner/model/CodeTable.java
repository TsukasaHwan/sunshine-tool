package org.sunshine.enums.scanner.model;

import org.sunshine.enums.core.enums.CodeEnum;

import java.util.List;

/**
 * @author: Teamo
 * @date: 2020/7/9 11:25
 * @description: 枚举码表缓存的数据结构
 */
public class CodeTable {

    /**
     * 枚举名称
     */
    private String enumName;

    /**
     * 枚举里面的所有枚举值
     */
    private List<CodeItem> items;

    /**
     * 默认的枚举值,默认是值为1的对象
     */
    private CodeEnum defaultItem;

    /**
     * 枚举的classPath,用来区分同名的枚举
     */
    private String classPath;


    public CodeTable(String enumName, List<CodeItem> items, CodeEnum defaultItem, String classPath) {
        this.enumName = enumName;
        this.items = items;
        this.defaultItem = defaultItem;
        this.classPath = classPath;
    }


    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public List<CodeItem> getItems() {
        return items;
    }

    public void setItems(List<CodeItem> items) {
        this.items = items;
    }

    public CodeEnum getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(CodeEnum defaultItem) {
        this.defaultItem = defaultItem;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
}
