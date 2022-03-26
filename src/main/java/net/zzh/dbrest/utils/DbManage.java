package net.zzh.dbrest.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.*;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.impl.MysqlDialect;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.handler.NumberHandler;
import cn.hutool.db.handler.PageResultHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.log.StaticLog;
import cn.hutool.log.level.Level;
import net.zzh.dbrest.DbRestPropertisHolder;
import net.zzh.dbrest.spring.SpringContextHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbManage {

   private static Db db;

   private static String dbType;

   public static Db getDb() {
        if (db != null) {
           return db;
        }
        DataSource dataSource = SpringContextHolder.getBean(DataSource.class);
        if (dataSource != null) {
            db = Db.use(dataSource);
            StaticLog.info("识别到DataSource数据源，使用datasource初始化。。。");
        }else {
            db = Db.use();
            StaticLog.info("未识别到DataSource，使用db.setting配置文件初始化。。。");
        }
        if (DbRestPropertisHolder.getDbRestPropertis().getShowSql()) {
            DbUtil.setShowSqlGlobal(true, false, true, Level.INFO);
        }
       if (db.getRunner().getDialect() instanceof MysqlDialect) {
           dbType = "mysql";
       }
        return db;
    }

    public static boolean isMysql() {
        return "mysql".equals(dbType);
    }

    public static List<Entity> findAll(Entity entity) throws SQLException {
        Connection conn = null;
        List<Entity> entities = new ArrayList<>();
        try {
            Db db = getDb();
            conn = db.getConnection();
            Query query = psQuery(Query.of(entity).setFields(entity.getFieldNames()));
            entities = db.getRunner().find(db.getConnection(), query,new EntityListHandler(true));
        } finally {
            db.closeConnection(conn);
        }
        return entities;
    }

    public static PageResult<Entity> page(Entity entity, Page page) throws SQLException {
        Connection conn = null;
        PageResult<Entity> pageResult = new PageResult<>();
        try {
            Db db = getDb();
            conn = db.getConnection();
            Query query = psQuery(Query.of(entity));
            Number count = SqlExecutor.queryAndClosePs(db.getRunner().getDialect().psForCount(conn, query), new NumberHandler(), new Object[0]);
            PageResultHandler pageResultHandler = new PageResultHandler(new PageResult(page.getPageNumber(), page.getPageSize(), count.intValue()), true);
            pageResult = (PageResult) db.getRunner().page(conn, query.setFields(entity.getFieldNames()).setPage(page), (RsHandler) pageResultHandler);
        }finally {
            db.closeConnection(conn);
        }
        return pageResult;
    }

    private static Query psQuery(Query query) {
        Condition[] where = query.getWhere();
        if (where == null) {
            return query;
        }
        for (Condition condition : where) {
            if (!StrUtil.equalsAnyIgnoreCase(condition.getOperator(),"like","between","in")) {
                condition.setOperator("");
            }
        }
        return query;
    }
}
