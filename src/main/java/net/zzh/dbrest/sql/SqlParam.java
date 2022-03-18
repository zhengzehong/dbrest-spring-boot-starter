package net.zzh.dbrest.sql;

import ognl.Ognl;
import ognl.OgnlException;

import java.util.Map;

class SqlParam {

    String key;
    String value;
    int order;

    public SqlParam(String key, int order) {
        this.key = key;
        this.order = order;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue(Map<String,Object> reqParams) {
        try {
            return  Ognl.getValue(key, reqParams);
        } catch (OgnlException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return key;
    }
}
