package org.sunshine.core.mp.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Teamo
 * @since 2020/6/8
 */
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    /**
     * 自动填充创建时间字段名
     */
    private String createFiledName = "gmtCreate";

    /**
     * 自动填充修改时间字段名
     */
    private String modifyFiledName = "gmtModified";

    /**
     * 自动填充逻辑删除字段名
     */
    private String isDeleteFiledName = "isDelete";

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter(createFiledName)) {
            this.strictInsertFill(metaObject, createFiledName, LocalDateTime.class, LocalDateTime.now());
        }
        if (metaObject.hasSetter(modifyFiledName)) {
            this.strictInsertFill(metaObject, modifyFiledName, LocalDateTime.class, LocalDateTime.now());
        }
        if (metaObject.hasSetter(isDeleteFiledName)) {
            this.strictInsertFill(metaObject, isDeleteFiledName, Boolean.class, Boolean.FALSE);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(modifyFiledName)) {
            this.strictUpdateFill(metaObject, modifyFiledName, LocalDateTime.class, LocalDateTime.now());
        }
    }

    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        Object obj = fieldVal.get();
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj);
        }
        return this;
    }

    public void setCreateFiledName(String createFiledName) {
        this.createFiledName = createFiledName;
    }

    public void setModifyFiledName(String modifyFiledName) {
        this.modifyFiledName = modifyFiledName;
    }

    public void setIsDeleteFiledName(String isDeleteFiledName) {
        this.isDeleteFiledName = isDeleteFiledName;
    }
}