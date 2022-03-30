package net.zzh.dbrest.annotation;

/**
 * @Description: 注解枚举类
 * @author Zeo Zheng
 * @date 2022/1/20 11:58
 * @version 1.0
 */
public enum DbQueryAnnotation {
    DBQUERY,
    DBQUERYPAGE,
    DBUPDATE,
    DBINSERT,
    DBQUERYSINGLE;

    public static DbQueryAnnotation get(Object annotation) {
        DbQueryAnnotation dbQueryAnnotation = null;
        if (annotation instanceof DbQuery) {
            dbQueryAnnotation = DbQueryAnnotation.DBQUERY;
        }else if (annotation instanceof DbInsert) {
            dbQueryAnnotation =  DbQueryAnnotation.DBINSERT;
        }else if (annotation instanceof DbUpdate) {
            dbQueryAnnotation =  DbQueryAnnotation.DBUPDATE;
        }else if (annotation instanceof DbQuerySingle) {
            dbQueryAnnotation = DbQueryAnnotation.DBQUERYSINGLE;
        }else if (annotation instanceof DbQueryPage) {
            dbQueryAnnotation = DbQueryAnnotation.DBQUERYPAGE;
        }
        return dbQueryAnnotation;
    }

    public static boolean isDbQueryAnnotation(Object annotation) {
        return get(annotation) != null;
    }
}
