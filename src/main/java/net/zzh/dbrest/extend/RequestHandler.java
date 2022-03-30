package net.zzh.dbrest.extend;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Description: 前置请求处理接口类
 * @author Zeo Zheng
 * @date 2022/1/23 16:58
 * @version 1.0
 */
public interface RequestHandler {

    /**
     * @param requestParams 请求的参数
     * @param annotation 接口注解
     * @param method 调用的方法
     */
    Map<String,Object> handler(Map<String, Object> requestParams, Object annotation, Method method);

}
