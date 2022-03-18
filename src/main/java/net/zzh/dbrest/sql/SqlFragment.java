package net.zzh.dbrest.sql;

import cn.hutool.core.collection.CollectionUtil;
import ognl.Ognl;
import ognl.OgnlException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

class SqlFragment {

    String originSql;

    String resultSql;

    Predicate<Map> fragmentFilter;

    List<SqlParam> statParams = new ArrayList<>(4);

    public SqlFragment(String originSql) {
        this.originSql = originSql;
        initStatParams(originSql);
        if (isBrackets()) {
            initResultSql(originSql.split("\\?")[1]);
            fragmentFilter = (params) -> {
                String condition = originSql.split("\\?")[0];
                try {
                    Object value = Ognl.getValue(condition, params);
                    if (value instanceof Boolean) {
                        return (Boolean) value;
                    }
                    if (value instanceof String) {
                        return !StringUtils.isEmpty(value);
                    }
                } catch (OgnlException e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            };
        }else{
            initResultSql(originSql);
            fragmentFilter = (params) -> true;
        }
    }

    public Predicate<Map> getFragmentFilter() {
        return fragmentFilter;
    }

    private void initStatParams(String originSql) {
        char[] chars = originSql.toCharArray();
        int itemIndex = 1;
        int start = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '[') {
                start = i;
            } else if (chars[i] == ']') {
                statParams.add(new SqlParam(new String(chars, start + 1, i - start - 1), itemIndex));
            }
        }
    }

    private void initResultSql(String originSql) {
        resultSql = originSql;
        if (CollectionUtil.isNotEmpty(statParams)) {
            statParams.stream().map(SqlParam::getKey).forEach(key -> {
                this.resultSql = resultSql.replace("[" + key + "]", "?");
            });
        }
    }


    public boolean isBrackets() {
        return originSql.contains("?");
    }

    public String getOriginSql() {
        return originSql;
    }

    public void setOriginSql(String originSql) {
        this.originSql = originSql;
    }

    public String getResultSql() {
        return resultSql;
    }

    public void setResultSql(String resultSql) {
        this.resultSql = resultSql;
    }

    public List<SqlParam> getStatParams() {
        return statParams;
    }

}
