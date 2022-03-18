package net.zzh.dbrest.annotation;

public enum DbQueryAnnotation {
    DBQUERY,
    DBQUERYPAGE,
    DBUPDATE,
    DBINSERT,
    DBQUERYSINGLE;
    public static DbQueryAnnotation get(Object annotation) {
        if (annotation instanceof DbQuery) {
            return DbQueryAnnotation.DBQUERY;
        }
        if (annotation instanceof DbInsert) {
            return DbQueryAnnotation.DBINSERT;
        }
        if (annotation instanceof DbUpdate) {
            return DbQueryAnnotation.DBUPDATE;
        }
        if (annotation instanceof DbQuerySingle) {
            return DbQueryAnnotation.DBQUERYSINGLE;
        }
        if (annotation instanceof DbQueryPage) {
            return DbQueryAnnotation.DBQUERYPAGE;
        }
        return null;
    }
    public static boolean isDbQueryAnnotation(Object annotation) {
        return get(annotation) != null;
    }
}
