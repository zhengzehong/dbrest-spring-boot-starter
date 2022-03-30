package net.zzh.dbrest.spring;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 代理工厂类
 * @author Zeo Zheng
 * @date 2022/1/23 16:58
 * @version 1.0
 */
public class DbRestProxyFactory {

    private static ConcurrentHashMap<Class,Object> proxyCache = new ConcurrentHashMap();

    public static  <T> T  getProxy (Class<T> tClass) {
       return (T) proxyCache.getOrDefault(tClass,getProxyInstance(tClass));
    }

    /**
     * 获取基于jdk proxy创建动态代理对象
     */
    private static synchronized <T> T getProxyInstance (Class<T> tClass) {
        Object instance = proxyCache.get(tClass);
        if (instance != null) {
            return (T)instance;
        }
        DbRestProxyHandler dbRestProxyHandler = new DbRestProxyHandler(tClass);
        instance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{tClass}, dbRestProxyHandler);
        proxyCache.put(tClass, instance);
        return (T) instance;
    }
}
