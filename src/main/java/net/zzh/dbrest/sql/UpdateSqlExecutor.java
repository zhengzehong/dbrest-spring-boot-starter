package net.zzh.dbrest.sql;

import cn.hutool.db.Db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateSqlExecutor extends AbstractSqlExecutor {
    @Override
    protected Object excuteSql(String sql, String group, Object[] sqlParams) throws SQLException {
        int affects = Db.use(group).execute(sql, sqlParams);
        return getResultMap(affects);
    }

    Map<String,Object> getResultMap(int affects) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", true);
        resultMap.put("affects", affects);
        return resultMap;
    }
}
