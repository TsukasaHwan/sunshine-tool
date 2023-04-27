package org.sunshine.enums.scanner.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.sunshine.enums.core.enums.CodeEnum;

/**
 * @author: Teamo
 * @date: 2020/7/9 11:25
 * @description: 枚举的数据值, 包括数值 code 和 标识名 name, 支持重写
 */
public class CodeItem implements CodeEnum {

    /**
     * code
     */
    private Integer code;

    /**
     * name 标识
     */
    private String name;

    public CodeItem(CodeEnum codeEnum) {
        this.code = codeEnum.code();
        this.name = codeEnum.value();
    }

    public CodeItem() {
    }

    /**
     * 重写覆盖 jsonValue注解,
     *
     * @return 枚举标识值
     */
    @Override
    @JsonValue(value = false)
    public Integer code() {
        return code;
    }

    @Override
    public String value() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
