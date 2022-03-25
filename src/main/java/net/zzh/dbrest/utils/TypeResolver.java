package net.zzh.dbrest.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.util.*;

/**
 * @Description: 类型转换器（数据库类型转Java类型）
 * @author Zeo Zheng
 * @CreateDate: 2020/11/01 15:26
 * @Version: 1.0
 */
public class TypeResolver {

    /**
     * 描述
     */
    private static Map<String, Class> typeMapping = new HashMap<>();

    static {
        addTypeMapping(CollUtil.newArrayList("varchar", "varchar2", "json", "text","longtext"), String.class);
        addTypeMapping(CollUtil.newArrayList("datetime", "date", "timestamp(6)", "timestamp"), Date.class);
        addTypeMapping(CollUtil.newArrayList("int", "integer", "number"), Integer.class);
        addTypeMapping(CollUtil.newArrayList("double"), double.class);
        addTypeMapping(CollUtil.newArrayList("float"), float.class);
    }

    /**
     * 描述
     * @param dbTypes
     * @param clazz
     * @author Zeo Zheng 
     */
    private static void addTypeMapping(List<String> dbTypes, Class clazz) {
        if (CollUtil.isNotEmpty(dbTypes)) {
            dbTypes.forEach(dbType -> typeMapping.put(dbType, clazz));
        }
    }

    public static Object toObject(String jdbcType, String value) {
        if (StrUtil.isEmpty(value)) {
            return "";
        }
        String javaType = getJavaType(jdbcType);
        if (javaType.equals("Date")) {
            return DateUtil.parse(value);
        }
        return value;
    }

    public static Object formate(String jdbcType, Object date) {
        String javaType = getJavaType(jdbcType);
        if (javaType.equals("Date")) {
            return DateUtil.formatDateTime((Date) date);
        }
        return date;
    }

    /**
     * 描述
     * @param dbType
     * @author Zeo Zheng 
     */
    public static String getJavaType(String dbType) {
        if (StrUtil.isEmpty(dbType)) {
            return "";
        }
        if (!typeMapping.containsKey(dbType.toLowerCase())) {
            return "";
        }else {
            return typeMapping.get(dbType.toLowerCase()).getSimpleName();
        }
    }
}
