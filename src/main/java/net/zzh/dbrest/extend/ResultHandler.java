package net.zzh.dbrest.extend;


import java.lang.reflect.Method;

/**
 * @Description: 请求结果处理接口类
 * @author Zeo Zheng
 * @date 2022/1/23 16:58
 * @version 1.0
 */
public interface ResultHandler {

    /**
     * @param result 当请求成功时result为返回的结果，当请求异常时，result为Excetion
     * @param annotation 注解对象
     * @param method 请求方法
     * @return 结果对象
     */
    Object handler(Object result, Object annotation, Method method);

}
