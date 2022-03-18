package net.zzh.dbrest;

import net.zzh.dbrest.extend.RequestHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import net.zzh.dbrest.extend.ResultHandler;

public class DbRestPropertisHolder {

    private static final Log log = LogFactory.getLog(DbRestPropertis.class);

    private static DbRestPropertis dbRestPropertis;

    private static ResultHandler globalResultHandler;

    private static RequestHandler globalRequestHandler;

    private static volatile boolean isResultHandlerInit = false;

    private static volatile boolean isRequestHandlerInit = false;

    public static void setDbRestPropertis(DbRestPropertis dbRestPropertis) {
        DbRestPropertisHolder.dbRestPropertis = dbRestPropertis;
    }

    public static DbRestPropertis getDbRestPropertis() {
        return dbRestPropertis;
    }

    public static ResultHandler getGlobalResultHandler() {
        if (!isResultHandlerInit && dbRestPropertis != null) {
            globalResultHandler = createResultHandler(dbRestPropertis.getResultHandler());
            isResultHandlerInit = true;
        }
        return globalResultHandler;
    }

    public static RequestHandler getGlobalRequestHandler() {
        if (!isRequestHandlerInit && dbRestPropertis != null) {
            globalRequestHandler = createRequestHandler(dbRestPropertis.getRequestHandler());
            isRequestHandlerInit = true;
        }
        return globalRequestHandler;
    }


    private static RequestHandler createRequestHandler(String requestHandlerClass) {
        if (!StringUtils.isEmpty(requestHandlerClass)) {
            try {

                Class<?> aClass = getClassLoader().loadClass(requestHandlerClass);
                if (!aClass.isInterface() && RequestHandler.class.isAssignableFrom(aClass)) {
                    return (RequestHandler) aClass.newInstance();
                } else {
                    log.error(aClass + "是一个接口或未实现RequestHandler接口！");
                }
            } catch (Exception e) {
                log.error("初始化RequestHandler实现类失败！", e);
            }
        }
        return null;

    }

    private static ClassLoader getClassLoader() {
        if (Thread.currentThread().getContextClassLoader() != null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return DbRestPropertisHolder.getClassLoader();
    }

    private static ResultHandler createResultHandler(String resultHandlerClass) {
        if (!StringUtils.isEmpty(resultHandlerClass)) {
            try {
                Class<?> aClass = getClassLoader().loadClass(resultHandlerClass);
                if (!aClass.isInterface() && ResultHandler.class.isAssignableFrom(aClass)) {
                    return (ResultHandler) aClass.newInstance();
                } else {
                    log.error(aClass + "是一个接口或未实现ResultHandler接口！");
                }
            } catch (Exception e) {
                log.error("初始化ResultHandler实现类失败！", e);
            }
        }
        return null;
    }

}