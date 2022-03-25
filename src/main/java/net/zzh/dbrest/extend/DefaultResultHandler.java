package net.zzh.dbrest.extend;


import java.lang.reflect.Method;

public class DefaultResultHandler implements ResultHandler {
    @Override
    public Object handler(Object result, Object annotation, Method method) {
        if (result instanceof Exception) {
            return new Response(false, ((Exception) result).getMessage(), null);
        }else {
            return new Response(true, "请求成功", result);
        }
    }
}
