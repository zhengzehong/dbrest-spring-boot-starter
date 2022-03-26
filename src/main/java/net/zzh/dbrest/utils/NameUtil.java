package net.zzh.dbrest.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

public class NameUtil {

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static String[]  getMethodNames(Method method) {
        return parameterNameDiscoverer.getParameterNames(method);
    }
}
