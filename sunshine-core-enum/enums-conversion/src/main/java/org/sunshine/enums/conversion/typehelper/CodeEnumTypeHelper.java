package org.sunshine.enums.conversion.typehelper;

import org.sunshine.enums.core.enums.CodeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author sneb
 * @description
 * @since 2022-11-10 16:07
 **/
@MappedJdbcTypes({JdbcType.INTEGER, JdbcType.BIGINT, JdbcType.SMALLINT, JdbcType.TINYINT})
public class CodeEnumTypeHelper<T extends CodeEnum> extends BaseTypeHandler<T> {

    private final Class<T> type;

    public CodeEnumTypeHelper(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        } else {
            this.type = type;
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            preparedStatement.setInt(i, parameter.code());
        } else {
            preparedStatement.setObject(i, parameter.code(), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        int s = resultSet.getInt(columnName);
        return CodeEnum.valueOf(this.type, s);
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        int s = resultSet.getInt(columnIndex);
        return CodeEnum.valueOf(this.type, s);
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        int s = callableStatement.getInt(columnIndex);
        return CodeEnum.valueOf(this.type, s);
    }
}
