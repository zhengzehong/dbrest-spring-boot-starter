package net.zzh.dbrest.sql;

import net.zzh.dbrest.spring.ExcuteMethodObj;

import java.sql.SQLException;
import java.util.Map;

public interface SqlExecutor {
    Object excute(ExcuteMethodObj excuteMethodObj, Map<String, Object> params) throws SQLException;
}
