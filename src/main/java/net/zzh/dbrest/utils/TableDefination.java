/**
 * Copyright (c) 2005, 2020, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package net.zzh.dbrest.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Description: 数据库表定义
 * @author Zeo Zheng
 * @CreateDate: 2020/10/30 11:43
 * @Version: 1.0
 */
public class TableDefination {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表字段
     */
    private List<Field> fields = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Optional<String> getJdbcType(String filedName) {
        return getFields().stream().filter(field -> field.getFieldName().equals(filedName.toLowerCase())).map(Field::getType).findFirst();
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
    /**
     * 表字段定义
     */
    public static class Field {
        /**
         * 字段名
         */
        private String fieldName;
        /**
         * 字段别名
         */
        private String alias;
        /**
         * 字段类型
         */
        private String type;
        /**
         * 字段长度
         */
        private int length;
        /**
         * 字段备注
         */
        private String remark;
        /**
         * 是否主键
         */
        private boolean isPrimary;



        public Field(String fieldName, String alias, String type, int length, String remark, boolean isPrimary) {
            this.fieldName = fieldName;
            this.alias = alias;
            this.type = type;
            this.length = length;
            this.remark = remark;
            this.isPrimary = isPrimary;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public boolean isPrimary() {
            return isPrimary;
        }

        public void setPrimary(boolean primary) {
            isPrimary = primary;
        }

        /**
         * freemarker中需要用get获取boolean
         * @return
         */
        public Boolean getIsPrimary() {
            return isPrimary;
        }
    }
}
