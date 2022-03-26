package net.zzh.dbrest.spring;

import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.extend.ResultHandler;
import net.zzh.dbrest.sql.SqlBuilder;
import net.zzh.dbrest.utils.NameUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExcuteMethodObj {

    private volatile DbQueryAnnotationHolder dbQueryAnnotationHolder;
    private ResultHandler resultHandler;
    private RequestHandler requestHandler;
    private SqlBuilder sqlBuilder;

    List<String> paramterNames;

    private Method excuteMethod;

    ExcuteMethodObj(Method excuteMethod) {
        this.excuteMethod = excuteMethod;
        dbQueryAnnotationHolder = new DbQueryAnnotationHolder(excuteMethod);
    }

    public DbQueryAnnotationHolder getDbQueryAnnotationHolder() {
        return dbQueryAnnotationHolder;
    }

    public SqlBuilder getSqlBuilder() {
        if (sqlBuilder == null) {
            sqlBuilder = createSqlBuilder();
        }
        if (sqlBuilder == null) {
            throw new IllegalStateException("该方法未定义ExcuteSql注解！");
        }
        return sqlBuilder;
    }

    private synchronized SqlBuilder createSqlBuilder() {
        if (sqlBuilder != null) {
            return sqlBuilder;
        }
       return new SqlBuilder(dbQueryAnnotationHolder.getSql());
    }

    public List<String> getParamterNames() {
        if (paramterNames == null) {
            paramterNames = parseParameterNames(excuteMethod);
        }
        return paramterNames;
    }

    public ResultHandler getResultHandler() {
        if (resultHandler == null) {
            resultHandler = createResultHandler();
        }
        return resultHandler;
    }

    private ResultHandler createResultHandler() {
        Class<ResultHandler> resultHandlerClass = dbQueryAnnotationHolder.getResultHandler();
        try {
            if (!resultHandlerClass.isInterface()) {
                resultHandler = resultHandlerClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultHandler;
    }

    public RequestHandler getRequestHandler() {
        if (requestHandler == null) {
            resultHandler = createResultHandler();
        }
        return requestHandler;
    }

    private RequestHandler createRequestHandler() {
        Class<RequestHandler> requestHandlerClass = dbQueryAnnotationHolder.getRequestHandler();
        try {
            if (!requestHandlerClass.isInterface()) {
                requestHandler = requestHandlerClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestHandler;
    }

    private synchronized List<String> parseParameterNames(Method method) {
        if (paramterNames != null) {
            return paramterNames;
        }
        paramterNames = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        String[] names = NameUtil.getMethodNames(method);
        for (int i = 0; i < parameters.length; i++) {
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            paramterNames.add(Optional.ofNullable(requestParam).map(RequestParam::value).filter(value -> !StringUtils.isEmpty(value)).orElse(names[i]));
        }
        return paramterNames;
    }

}
