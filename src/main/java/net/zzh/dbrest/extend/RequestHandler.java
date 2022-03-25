package net.zzh.dbrest.extend;

import java.lang.reflect.Method;
import java.util.Map;

public interface RequestHandler {

    Map<String,Object> handler(Map<String, Object> requestParams, Object annotation, Method method);

}
