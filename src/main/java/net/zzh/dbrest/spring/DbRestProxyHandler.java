package net.zzh.dbrest.spring;

import net.zzh.dbrest.DbRestPropertisHolder;
import net.zzh.dbrest.annotation.DbQueryAnnotation;
import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.extend.ResultHandler;
import net.zzh.dbrest.sql.SqlExecutorManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DbRestProxyHandler<T> implements InvocationHandler {

    private Class<T> proxyInterface;

    private ConcurrentHashMap<Method, ExcuteMethodObj> methodCache = new ConcurrentHashMap();

    public DbRestProxyHandler(Class proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isDbQueryMethod(method)) {
            ExcuteMethodObj excuteMethodObj = getExcuteMethodObj(method);
            Map<String, Object> paramMap = createParamMap(excuteMethodObj.getParamterNames(), args);
            //执行Sql前置回调
            paramMap = invokeRequestHandler(paramMap, excuteMethodObj.getRequestHandler());
            //执行sql
            Object result = SqlExecutorManager.excute(excuteMethodObj, paramMap);
            //执行Sql后置回调
            return invokeResultHandler(result, excuteMethodObj.getDbQueryAnnotationHolder(), excuteMethodObj.getResultHandler());
        }
        return null;
    }

    private Map<String,Object> invokeRequestHandler(Map<String,Object> requestParams, RequestHandler requestHandler) {
        if (requestHandler != null) {
            return requestHandler.handler(requestParams);
        } else if (DbRestPropertisHolder.getGlobalRequestHandler() != null) {
            return DbRestPropertisHolder.getGlobalRequestHandler().handler(requestParams);
        }
        return requestParams;
    }

    private Object invokeResultHandler(Object result, DbQueryAnnotationHolder dbQueryAnnotationHolder, ResultHandler resultHandler) {
        if (resultHandler != null) {
            return resultHandler.handler(result,dbQueryAnnotationHolder);
        } else if (DbRestPropertisHolder.getGlobalResultHandler() != null) {
            return DbRestPropertisHolder.getGlobalResultHandler().handler(result,dbQueryAnnotationHolder);
        }
        return result;
    }

    ExcuteMethodObj getExcuteMethodObj(Method method) {
        ExcuteMethodObj excuteMethodObj = methodCache.get(method);
        if (excuteMethodObj == null) {
            synchronized (this) {
                excuteMethodObj = methodCache.get(method);
                if (excuteMethodObj != null) {
                    return excuteMethodObj;
                }else {
                    excuteMethodObj = new ExcuteMethodObj(method);
                    methodCache.put(method, excuteMethodObj);
                }
            }
        }
        return excuteMethodObj;
    }

    boolean isDbQueryMethod(Method method) {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if(DbQueryAnnotation.isDbQueryAnnotation(annotation)){
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> createParamMap(List<String> paramterNames, Object[] args) {
        Map<String, Object> paramsMap = new HashMap();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                paramsMap.put(paramterNames.get(i), args[i]);
                if ( args[i] instanceof Map) {
                    paramsMap.putAll((Map<? extends String, ?>) args[0]);
                }
            }
        }
        return paramsMap;
    }
}

