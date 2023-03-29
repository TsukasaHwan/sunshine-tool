package com.sunshine.core.log.model;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author Teamo
 * @since 2023/01/13
 */
@TableName("sys_log_api")
public class LogApi extends LogAbstract {

    /**
     * 日志标题
     */
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
