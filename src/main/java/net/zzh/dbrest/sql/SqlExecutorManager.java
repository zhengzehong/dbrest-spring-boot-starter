package net.zzh.dbrest.sql;

import net.zzh.dbrest.annotation.DbQueryAnnotation;
import net.zzh.dbrest.spring.ExcuteMethodObj;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SqlExecutorManager {

    private static Map<DbQueryAnnotation, SqlExecutor> sqlExecutors = new HashMap<>();

    static {
        sqlExecutors.put(DbQueryAnnotation.DBINSERT, new InsertSqlExecutor());
        sqlExecutors.put(DbQueryAnnotation.DBUPDATE, new UpdateSqlExecutor());
        sqlExecutors.put(DbQueryAnnotation.DBQUERYSINGLE, new QuerySingleSqlExecutor());
        sqlExecutors.put(DbQueryAnnotation.DBQUERY, new QuerySqlExecutor());
        sqlExecutors.put(DbQueryAnnotation.DBQUERYPAGE, new QueryPageSqlExecutor());
    }

    public static Object excute(ExcuteMethodObj excuteMethodObj, Map<String, Object> params) throws SQLException {
        SqlExecutor sqlExecutor = getSqlExecutor(excuteMethodObj.getDbQueryAnnotationHolder().getDbQueryAnnotation());
        if (sqlExecutor != null) {
            return sqlExecutor.excute(excuteMethodObj, params);
        }
        return null;
    }

    public static SqlExecutor getSqlExecutor(DbQueryAnnotation dbQueryAnnotation) {
       return sqlExecutors.get(dbQueryAnnotation);
    }
}
