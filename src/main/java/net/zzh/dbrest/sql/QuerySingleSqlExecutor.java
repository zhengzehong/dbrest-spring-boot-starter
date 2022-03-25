package net.zzh.dbrest.sql;

import cn.hutool.db.Entity;
import net.zzh.dbrest.utils.DbManage;
import net.zzh.dbrest.utils.EntityUtils;

import java.sql.SQLException;

public class QuerySingleSqlExecutor extends AbstractSqlExecutor {
    @Override
    protected Object excuteSql(String sql, Object[] sqlParams) throws SQLException {
        Entity entity = DbManage.getDb().queryOne(sql, sqlParams);
        if (entity != null) {
            return EntityUtils.entityToMap(entity, true);
        }
        return entity;
    }
}
