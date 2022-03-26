package net.zzh.dbrest.sql;

import java.util.*;
import java.util.stream.Collectors;

public class SqlBuilder {

    String originSql;

    List<SqlFragment> sqlFragments;

    public SqlBuilder(String originSql) {
        this.originSql = originSql;
        this.sqlFragments = initSqlFragment(originSql);
    }
    public String getSqlStatment(Map<String,Object> requestParams) {
        return sqlFragments.stream().filter((sqlFragment -> sqlFragment.getFragmentFilter().test(requestParams)))
                .map(sqlFragment -> sqlFragment.getResultSql(requestParams)).reduce((t1, t2) -> t1 + t2).get();
    }

    public Object[] getSqlParams(Map<String,Object> requestParams) {
        return sqlFragments.stream().filter((sqlFragment -> sqlFragment.getFragmentFilter().test(requestParams)))
                .flatMap((SqlFragment sqlFragment) -> sqlFragment.getStatParams().stream()).filter(sqlParam -> !sqlParam.isInsertSql)
                .map(sqlParam -> sqlParam.getValue(requestParams)).toArray();
    }



    public static List<SqlFragment> initSqlFragment(String sql) {
        return Arrays.stream(sql.split("\\{")).flatMap(text -> Arrays.stream(text.split("\\}"))).map(SqlFragment::new).collect(Collectors.toList());
    }

}
