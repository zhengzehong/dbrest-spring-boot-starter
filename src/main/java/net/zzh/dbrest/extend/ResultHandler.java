package net.zzh.dbrest.extend;


import net.zzh.dbrest.spring.DbQueryAnnotationHolder;

public interface ResultHandler {

    Object handler(Object result, DbQueryAnnotationHolder dbQueryAnnotationHolder);

}
