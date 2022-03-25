package net.zzh.dbrest.sql;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.db.Entity;
import net.zzh.dbrest.utils.DbManage;
import net.zzh.dbrest.utils.EntityUtils;

import java.sql.SQLException;
import java.util.List;

public class QuerySqlExecutor extends AbstractSqlExecutor {
    @Override
    protected Object excuteSql(String sql, Object[] sqlParams) throws SQLException {
        List<Entity> results = DbManage.getDb().query(sql, sqlParams);
        if (CollectionUtil.isNotEmpty(results)) {
            return EntityUtils.entitiesToMapList(results);
        }
        return results;
    }
}
