package net.zzh.dbrest.spring;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.StaticLog;
import net.zzh.dbrest.DbRestPropertisHolder;
import net.zzh.dbrest.annotation.DbQueryAnnotation;
import net.zzh.dbrest.extend.DefaultResultHandler;
import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.extend.ResultHandler;
import net.zzh.dbrest.sql.SqlExecutorManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 代理处理类
 * @author Zeo Zheng
 * @date 2022/1/23 16:58
 * @version 1.0
 */
public class DbRestProxyHandler<T> implements InvocationHandler {

    /**
     * 动态代理的接口
     */
    private Class<T> proxyInterface;

    private ConcurrentHashMap<Method, ExcuteMethodObj> methodCache = new ConcurrentHashMap();

    public DbRestProxyHandler(Class proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isDbQueryMethod(method)) {
            ExcuteMethodObj excuteMethodObj = getExcuteMethodObj(method);
            try {
                Map<String, Object> paramMap = createParamMap(excuteMethodObj.getParamterNames(), args);
                //执行Sql前置回调
                paramMap = invokeRequestHandler(paramMap, excuteMethodObj.getRequestHandler(), excuteMethodObj.getDbQueryAnnotationHolder(), method);
                //执行sql处理伙计
                Object result = SqlExecutorManager.excute(excuteMethodObj, paramMap);
                //执行Sql后置回调
                return invokeResultHandler(result, excuteMethodObj.getDbQueryAnnotationHolder(), excuteMethodObj.getResultHandler(),method);
            } catch (Exception e) {
                StaticLog.error(e,"请求异常");
                return invokeResultHandler(e, excuteMethodObj.getDbQueryAnnotationHolder(), excuteMethodObj.getResultHandler(),method);
            }
        }else {
            return method.invoke(this, args);
        }
    }

    private Map<String,Object> invokeRequestHandler(Map<String,Object> requestParams, RequestHandler requestHandler,DbQueryAnnotationHolder dbQueryAnnotationHolder,Method method) {
        if (requestHandler != null) {
            return requestHandler.handler(requestParams,dbQueryAnnotationHolder.getAnnotation(),method);
        } else if (DbRestPropertisHolder.getGlobalRequestHandler() != null) {
            return DbRestPropertisHolder.getGlobalRequestHandler().handler(requestParams, dbQueryAnnotationHolder.getAnnotation(), method);
        }
        return requestParams;
    }

    private Object invokeResultHandler(Object result, DbQueryAnnotationHolder dbQueryAnnotationHolder, ResultHandler resultHandler,Method method) {
        if (DbRestPropertisHolder.getGlobalResultHandler() != null && resultHandler instanceof DefaultResultHandler) {
            return DbRestPropertisHolder.getGlobalResultHandler().handler(result,dbQueryAnnotationHolder.getAnnotation(),method);
        } else if (resultHandler != null) {
            return resultHandler.handler(result,dbQueryAnnotationHolder.getAnnotation(),method);
        }else {
            return result;
        }
    }

    private ExcuteMethodObj getExcuteMethodObj(Method method) {
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

    /**
     * 把请求参数转成map形式
     * @param paramterNames
     * @param args
     * @return
     */
    private Map<String, Object> createParamMap(List<String> paramterNames, Object[] args) {
        Map<String, Object> paramsMap = new HashMap();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                //把map对象扩展
                if ( args[i] instanceof Map) {
                    paramsMap.putAll((Map<? extends String, ?>) args[i]);
                }
                //支持bean
                if (args[i] != null && BeanUtil.isBean(args[i].getClass())) {
                    paramsMap.putAll(BeanUtil.beanToMap(args[i]));
                }
                paramsMap.put(paramterNames.get(i), args[i] == null ? "" : args[i]);
            }
        }
        return paramsMap;
    }
}

