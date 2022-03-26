package net.zzh.dbrest.sql;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import ognl.Ognl;
import ognl.OgnlException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

class SqlFragment {

    String originSql;

    //String resultSql;

    Predicate<Map> fragmentFilter;

    List<SqlParam> statParams = new ArrayList<>(4);

    public SqlFragment(String originSql) {
        this.originSql = originSql;
        initStatParams(originSql);
        if (isBrackets()) {
            //initResultSql(originSql.split("\\?")[1]);
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
            //initResultSql(originSql);
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
        boolean isInsertSql = false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '[') {
                start = i;
                isInsertSql = chars[i > 0 ? i - 1 : 0] == '$';
            } else if (chars[i] == ']') {
                statParams.add(new SqlParam(new String(chars, start + 1, i - start - 1), itemIndex, isInsertSql));
            }
        }
    }

    public String getResultSql(Map<String,Object> data) {
        String resultSql = isBrackets() ? originSql.split("\\?")[1] : this.originSql;
        if (CollectionUtil.isNotEmpty(statParams)) {
            for (SqlParam sqlParam : statParams) {
                if (!sqlParam.isInsertSql) {
                    resultSql = resultSql.replace("[" + sqlParam.getKey() + "]", "?");
                }else {
                    Object value = sqlParam.getValue(data);
                    Assert.isTrue(value instanceof String, "获取的sql片段值必须是字符串类型");
                    resultSql = resultSql.replace("$[" + sqlParam.getKey() + "]", StrUtil.nullToEmpty((String) value));
                }
            };
        }
        return resultSql;
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

    public List<SqlParam> getStatParams() {
        return statParams;
    }

}
