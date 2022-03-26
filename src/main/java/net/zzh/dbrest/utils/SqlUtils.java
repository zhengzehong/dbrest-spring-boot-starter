package net.zzh.dbrest.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtils {

    static Map<String,String> conditionCharMap = new HashMap();

    static {
        conditionCharMap.put("eq", "=");
        conditionCharMap.put("neq", "<>");
        conditionCharMap.put("like", "like");
        conditionCharMap.put("in", "in");
        conditionCharMap.put("gt", ">");
        conditionCharMap.put("gte", ">=");
        conditionCharMap.put("lt", "<");
        conditionCharMap.put("lte", "<=");
        conditionCharMap.put("bet", "between");
    }

    /**
     * 校验sql注入
     * @param obj
     * @return
     */
    public static boolean containsSqlInjection(String obj){
        if (StrUtil.isEmpty(obj)) {
            return false;
        }
        Pattern pattern= Pattern.compile("\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)");
        Matcher matcher=pattern.matcher(obj.toString().toLowerCase());
        return matcher.find();
    }

    public static Optional<String[]> getConditionChar(String key,String splitChar) {
        if (StrUtil.isEmpty(key) || !key.contains(splitChar)) {
            return Optional.of(new String[]{key + " =", ""});
        }
        key = key.trim();
        int i = key.lastIndexOf(splitChar);
        String condition = conditionCharMap.get(key.substring(i + 1, key.length()).toLowerCase());
        if (StrUtil.isEmpty(condition)) {
            return Optional.of(new String[]{key + " =", ""});
        } else if ("between".equals(condition) || "like".equals(condition) || "in".equals(condition)) {
            return Optional.of(new String[]{key.substring(0, i), conditionCharMap.get(key.substring(i + 1, key.length()).toLowerCase())});
        } else {
            return Optional.of(new String[]{key.substring(0, i) + " " + conditionCharMap.get(key.substring(i + 1, key.length()).toLowerCase()), ""});
        }
    }

    public static String getSplitKey(String key,String splitChar) {
        if (StrUtil.isEmpty(key) || !key.contains(splitChar)) {
            return key;
        }
        key = key.trim();
        int i = key.lastIndexOf(splitChar);
        String condition = conditionCharMap.get(key.substring(i + 1, key.length()).toLowerCase());
        if (StrUtil.isEmpty(condition)) {
            return key;
        } else {
            return key.substring(0, i);
        }
    }

    public static String wapperParams(String conditionChar, String params) {
        switch (conditionChar) {
            case "in" :
                return "in " + params;
            case "like":
                return "like %" + params + "%";
            case "between":
                String[] split = params.split(",");
                Assert.isTrue(split.length == 2);
                return "between " + split[0] + " and " + split[1];
           /* case "=":
                return params;*/
            default:
                return params;
        }
    }

}
