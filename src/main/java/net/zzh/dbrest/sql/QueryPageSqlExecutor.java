package net.zzh.dbrest.sql;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import net.zzh.dbrest.utils.EntityUtils;
import net.zzh.dbrest.page.Page;
import net.zzh.dbrest.page.PageResult;

import java.sql.SQLException;
import java.util.List;

public class QueryPageSqlExecutor extends AbstractSqlExecutor {

    @Override
    protected Object excuteSql(String sql, String group, Object[] sqlParams) throws SQLException {
        String countSql = "SELECT count(*) from (" + sql + ") dbresttmptable123";
        String querySql = "SELECT * from (" + sql + " ) dbresttmptable123 limit ?,?";
        Page page = getPage();
        PageResult pageResult = new PageResult(page.getPageNumber(), page.getPageSize());
        Number totalNumer = Db.use().queryNumber(countSql, sqlParams);
        List<Entity> datas = Db.use().query(querySql, ArrayUtil.append(sqlParams,page.getStartPosition(),page.getEndPosition()));
        pageResult.setTotal(totalNumer.intValue());
        pageResult.setDatas(EntityUtils.entitiesToMapList(datas));
        return pageResult;
    }

    private Page getPage() {
        if (CollectionUtil.isNotEmpty(getParams())) {
            return (Page) getParams().values().stream().filter(t -> t instanceof Page).findFirst().orElse(new Page());
        }
        return new Page();
    }

}
