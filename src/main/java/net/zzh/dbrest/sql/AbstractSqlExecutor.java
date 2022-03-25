package net.zzh.dbrest.sql;

import net.zzh.dbrest.spring.ExcuteMethodObj;

import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractSqlExecutor implements SqlExecutor {

    protected ExcuteMethodObj excuteMethodObj;

    protected Map<String, Object> params;

    @Override
    public Object excute(ExcuteMethodObj excuteMethodObj, Map<String, Object> params) throws SQLException {
        this.excuteMethodObj = excuteMethodObj;
        this.params = params;
        SqlBuilder sqlBuilder = excuteMethodObj.getSqlBuilder();
        String sql = sqlBuilder.getSqlStatment(params);
        Object[] sqlParams = sqlBuilder.getSqlParams(params);
        return excuteSql(sql,sqlParams);
    }

    protected abstract Object excuteSql(String sql, Object[] sqlParams) throws SQLException;

    protected ExcuteMethodObj getExcuteMethodObj() {
        return excuteMethodObj;
    }

    protected Map<String, Object> getParams() {
        return params;
    }
}
