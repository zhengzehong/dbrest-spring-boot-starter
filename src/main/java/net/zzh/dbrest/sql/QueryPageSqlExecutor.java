package net.zzh.dbrest.sql;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Entity;
import net.zzh.dbrest.utils.DbManage;
import net.zzh.dbrest.utils.EntityUtils;
import net.zzh.dbrest.page.Page;
import net.zzh.dbrest.page.PageResult;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryPageSqlExecutor extends AbstractSqlExecutor {

    @Override
    protected Object excuteSql(String sql, Object[] sqlParams) throws SQLException {
        Page page = getPage();
        cn.hutool.db.Page page1 = new cn.hutool.db.Page(page.getPage() - 1, page.getSize());
        cn.hutool.db.PageResult<Entity> pageResult2 = DbManage.getDb().page(sql, page1, Arrays.stream(sqlParams).filter(t-> (!(t instanceof Page) && !(t instanceof Page)))
                .collect(Collectors.toList()).toArray());
        PageResult pageResult = new PageResult(page.getPage(), page.getSize());
        pageResult.setTotal(pageResult2.getTotal());
        pageResult.setDatas(EntityUtils.entitiesToMapList(pageResult2));
        return pageResult;
    }

    private Page getPage() {
        if (CollectionUtil.isNotEmpty(getParams())) {
            return (Page) getParams().values().stream().filter(t -> t instanceof Page).findFirst().orElse(new Page());
        }else if (CollectionUtil.isNotEmpty(getParams())) {
            Optional<Object> first = getParams().values().stream().filter(t -> t instanceof cn.hutool.db.Page).findFirst();
            Page page = new Page();
            first.ifPresent( p -> {
                page.setPage(((cn.hutool.db.Page)p).getPageNumber()-1);
                page.setSize(((cn.hutool.db.Page) p).getPageSize());
            });
            return page;
        }
        return new Page();
    }

}
