package org.sunshine.core.mp.extension.plugin;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.StringUtils;

/**
 * @author Teamo
 * @since 2023/5/5
 */
public class DynamicTableNameHandler implements TableNameHandler {

    private String delimiter = StringPool.UNDERSCORE;

    @Override
    public String dynamicTableName(String sql, String tableName) {
        String suffix = DynamicTableSuffixContextHolder.getTableNameSuffix();
        return StringUtils.isEmpty(suffix) ? tableName : tableName + delimiter + suffix;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
