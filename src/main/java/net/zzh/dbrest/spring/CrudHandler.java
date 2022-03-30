package net.zzh.dbrest.spring;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import net.zzh.dbrest.annotation.DbCrud;
import net.zzh.dbrest.sql.CrudAction;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description: CrudHandler集成ApplicationListener，用于注册@DbCrud注解接口
 * @author Zeo Zheng
 * @date 2022/1/23 16:58
 * @version 1.0
 */
public class CrudHandler implements ApplicationListener<ApplicationReadyEvent> {

    private static Set<Method> crudMethods = new HashSet();

    private static List<String> initMethods = ListUtil.of("getById", "save", "findList", "findPage", "delete");

    static void addCrudMethod(Method method) {
        crudMethods.add(method);
    }

    /**
     * 在应用启动后注册DbCrud接口
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //获取spring的RequestMappingHandlerMapping，用于注册
        RequestMappingHandlerMapping requestMappingHandlerMapping = SpringContextHolder.getBean(RequestMappingHandlerMapping.class);
        for (Method crudMethod : crudMethods) {
            DbCrud annotation = crudMethod.getAnnotation(DbCrud.class);
            CrudAction crudAction = new CrudAction(annotation);
            Class<?> declaringClass = crudMethod.getDeclaringClass();
            //获取类上的RequestMapping注解
            RequestMapping requestMapping = declaringClass.getAnnotation(RequestMapping.class);
            PatternsRequestCondition typeCondition = null;
            if (requestMapping != null) {
                typeCondition = new PatternsRequestCondition(requestMapping.value());
            }
            List<String> requests = new ArrayList<>();
            for (String initMethod : initMethods) {
                Method getMethod = ReflectionUtils.findMethod(CrudAction.class, initMethod, HttpServletRequest.class);
                RequestMethodsRequestCondition requestMethodsRequestCondition = null;
                switch (initMethod) {
                    case "save":
                        requestMethodsRequestCondition = new RequestMethodsRequestCondition(RequestMethod.POST,RequestMethod.PUT);
                        break;
                    case "delete":
                        requestMethodsRequestCondition = new RequestMethodsRequestCondition(RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST);
                        break;
                    default:
                        requestMethodsRequestCondition = new RequestMethodsRequestCondition(RequestMethod.GET,RequestMethod.POST);
                }
                //spring创建spring请求路径匹配对象，默认/tablename/save(update等)
                PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition("/" + StrUtil.toCamelCase(annotation.tableName().toLowerCase()) + "/" + initMethod);
                if (typeCondition != null) {
                    //如果类上有RequectMapping的注解，则联合
                    patternsRequestCondition = typeCondition.combine(patternsRequestCondition);
                }
                requests.addAll(patternsRequestCondition.getPatterns());
                RequestMappingInfo requestMappingInfo = new RequestMappingInfo(patternsRequestCondition
                        , requestMethodsRequestCondition, null, null, null, null, null);
                //注册spring mvc请求
                requestMappingHandlerMapping.registerMapping((RequestMappingInfo) requestMappingInfo, crudAction, getMethod);
            }
            StaticLog.info("@DbCrud请求：" + requests);
        }
    }

}
