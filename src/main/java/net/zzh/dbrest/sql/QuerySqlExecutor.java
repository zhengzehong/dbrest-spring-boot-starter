package net.zzh.dbrest.sql;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import net.zzh.dbrest.utils.EntityUtils;

import java.sql.SQLException;
import java.util.List;

public class QuerySqlExecutor extends AbstractSqlExecutor {
    @Override
    protected Object excuteSql(String sql, String group, Object[] sqlParams) throws SQLException {
        List<Entity> results = Db.use(group).query(sql, sqlParams);
        if (CollectionUtil.isNotEmpty(results)) {
            return EntityUtils.entitiesToMapList(results);
        }
        return results;
    }
}
