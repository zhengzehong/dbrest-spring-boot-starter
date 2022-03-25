package net.zzh.dbrest.sql;

import net.zzh.dbrest.utils.DbManage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateSqlExecutor extends AbstractSqlExecutor {
    @Override
    protected Object excuteSql(String sql, Object[] sqlParams) throws SQLException {
        int affects = DbManage.getDb().execute(sql, sqlParams);
        return getResultMap(affects);
    }

    Map<String,Object> getResultMap(int affects) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("effects", affects);
        return resultMap;
    }
}
