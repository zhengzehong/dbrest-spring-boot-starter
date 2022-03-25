package net.zzh.dbrest.sql;

import cn.hutool.core.util.IdUtil;
import cn.hutool.db.Db;
import net.zzh.dbrest.annotation.DbInsert;
import net.zzh.dbrest.spring.ExcuteMethodObj;
import net.zzh.dbrest.utils.DbManage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InsertSqlExecutor extends AbstractSqlExecutor {

    @Override
    protected Object excuteSql(String sql, Object[] sqlParams) throws SQLException {
        Map<String, Object> resultMap = new HashMap<>();
        DbInsert dbInsert = (DbInsert)this.getExcuteMethodObj().getDbQueryAnnotationHolder().getAnnotation();
        switch (dbInsert.idtype()) {
            case  AUTO:
                Long aLong1 = DbManage.getDb().executeForGeneratedKey(sql, sqlParams);
                resultMap.put("autoKey", aLong1);
                break;
            default:
                DbManage.getDb().execute(sql, sqlParams);
                break;
        }
        return resultMap;
    }
}
