package net.zzh.dbrest.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 数据库表定义生成
 * @author Zeo Zheng
 * @CreateDate: 2020/10/30 11:58
 * @Version: 1.0
 */
@Component
public class DbTableManage {

    /**
     * 描述
     * @param tableName
     * @author Zeo Zheng
     */
    public static TableDefination getTableDefination(String tableName) throws Exception {
        return createTableDefination(tableName, DbManage.getDb().getConnection());
    }

    /**
     * 创建表定义信息
     * @param tableName
     * @param connection
     * @return
     * @throws Exception
     */
    private static TableDefination createTableDefination(String tableName, Connection connection) throws Exception {
        DatabaseMetaData metaData = connection.getMetaData();
        TableDefination tableDefination = new TableDefination();
        tableDefination.setTableName(tableName);
        List<TableDefination.Field> tableFields = createTableFields(tableName, metaData);
        markPrimaryKey(tableName, tableFields, metaData);
        tableDefination.setFields(tableFields);
        return tableDefination;
    }

    /**
     * 标识主键
     */
    private static void markPrimaryKey(String tableName, List<TableDefination.Field> tableFields, DatabaseMetaData metaData) throws SQLException {
        if (CollectionUtil.isEmpty(tableFields)) {
            return;
        }
        ResultSet pkRSet = metaData.getPrimaryKeys(null, null, tableName);
        while (pkRSet.next()) {
            String columnName = pkRSet.getString("COLUMN_NAME");
            tableFields.stream().filter(tableField -> columnName.equals(tableField.getFieldName()))
                    .forEach(tableField -> tableField.setPrimary(true));
        }
    }

    /**
     * 创建表字段
     * @param tableName
     * @param metaData
     * @return
     * @throws SQLException
     */
    private static List<TableDefination.Field> createTableFields(String tableName, DatabaseMetaData metaData) throws SQLException {
        List<TableDefination.Field> fields = new ArrayList<>();
        ResultSet colRet = metaData.getColumns(null, "%", tableName, "%");
        while (colRet.next()) {
            TableDefination.Field field = new TableDefination.Field(colRet.getString("COLUMN_NAME").toLowerCase(), StrUtil.toCamelCase(colRet.getString("COLUMN_NAME").toLowerCase()),
                     colRet.getString("TYPE_NAME").toLowerCase(),
                    colRet.getInt("COLUMN_SIZE"), colRet.getString("REMARKS"), false);
            fields.add(field);
        }
        return fields;
    }

    public static void main(String[] args) throws Exception {
        TableDefination table_config = getTableDefination("BOOK2");
    }
}
