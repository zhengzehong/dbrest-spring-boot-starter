package net.zzh.dbrest.sql;

import cn.hutool.core.util.IdUtil;
import cn.hutool.db.Db;
import net.zzh.dbrest.annotation.DbInsert;
import net.zzh.dbrest.spring.ExcuteMethodObj;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InsertSqlExecutor extends AbstractSqlExecutor {

    @Override
    protected Object excuteSql(String sql, String group, Object[] sqlParams) throws SQLException {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", true);
        DbInsert dbInsert = (DbInsert)this.getExcuteMethodObj().getDbQueryAnnotationHolder().getAnnotation();
        switch (dbInsert.idtype()) {
            case  AUTO:
                Long aLong1 = Db.use(group).executeForGeneratedKey(sql, sqlParams);
                resultMap.put("autoKey", aLong1);
                break;
            default:
                Db.use(group).execute(sql, sqlParams);
                break;
        }
        return resultMap;
    }
}
