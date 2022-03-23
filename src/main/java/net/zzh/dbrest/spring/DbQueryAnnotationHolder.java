package net.zzh.dbrest.spring;

import cn.hutool.core.util.ReflectUtil;
import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.annotation.DbQueryAnnotation;
import net.zzh.dbrest.extend.ResultHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DbQueryAnnotationHolder {

    Object annotation;

    DbQueryAnnotation dbQueryAnnotation;

    public DbQueryAnnotationHolder(Object annotation) {
        this.annotation = annotation;
        this.dbQueryAnnotation = DbQueryAnnotation.get(annotation);
    }

    public DbQueryAnnotationHolder(Method method) {
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        for (int i = 0; i < method.getDeclaredAnnotations().length; i++) {
            if(DbQueryAnnotation.isDbQueryAnnotation(declaredAnnotations[i])){
                this.annotation = declaredAnnotations[i];
            }
        }
        if (this.annotation == null) {
            throw new IllegalStateException(method.getName() + "方法未添加查询注解！");
        }
        this.dbQueryAnnotation = DbQueryAnnotation.get(annotation);
    }

    public String getSql() {
       return  ReflectUtil.invoke(annotation, "value");
    }

    public String getGroup() {
        return  ReflectUtil.invoke(annotation, "group");
    }

    public Class<ResultHandler> getResultHandler() {
        return  ReflectUtil.invoke(annotation, "resultHandler");
    }

    public Class<RequestHandler> getRequestHandler() {
        return  ReflectUtil.invoke(annotation, "requestHandler");
    }

    public DbQueryAnnotation getDbQueryAnnotation() {
        return dbQueryAnnotation;
    }

    public Object getAnnotation() {
        return annotation;
    }
}
