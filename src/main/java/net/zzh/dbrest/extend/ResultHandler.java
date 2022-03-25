package net.zzh.dbrest.extend;


import net.zzh.dbrest.spring.DbQueryAnnotationHolder;

import java.lang.reflect.Method;

public interface ResultHandler {

    Object handler(Object result, Object annotation, Method method);

}
