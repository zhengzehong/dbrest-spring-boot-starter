package net.zzh.dbrest.spring;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.sun.org.apache.regexp.internal.RE;
import net.zzh.dbrest.annotation.DbCrud;
import net.zzh.dbrest.sql.CrudAction;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
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
 * 来源微信公众号：Java技术栈
 */
public class CrudHandler implements ApplicationListener<ApplicationReadyEvent> {

    private static Set<Method> crudMethods = new HashSet();

    private static List<String> initMethods = ListUtil.of("getById", "delete", "findList", "findPage", "save");

    static void addCrudMethod(Method method) {
        crudMethods.add(method);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //if (ReadinessState.ACCEPTING_TRAFFIC == event.event()){
            RequestMappingHandlerMapping requestMappingHandlerMapping = SpringContextHolder.getBean(RequestMappingHandlerMapping.class);
            for (Method crudMethod : crudMethods) {
                DbCrud annotation = crudMethod.getAnnotation(DbCrud.class);
                CrudAction crudAction = new CrudAction(annotation);
                Class<?> declaringClass = crudMethod.getDeclaringClass();

                RequestMapping requestMapping = declaringClass.getAnnotation(RequestMapping.class);
                PatternsRequestCondition typeCondition = null;
                if (requestMapping != null) {
                    typeCondition = new PatternsRequestCondition(requestMapping.value());
                }
                for (String initMethod : initMethods) {
                    Method getMethod = ReflectionUtils.findMethod(CrudAction.class, initMethod, HttpServletRequest.class);
                    RequestMethodsRequestCondition requestMethodsRequestCondition = null;
                    switch (initMethod) {
                        case "save":
                            requestMethodsRequestCondition = new RequestMethodsRequestCondition(RequestMethod.POST);
                            break;
                        default:
                            requestMethodsRequestCondition = new RequestMethodsRequestCondition(RequestMethod.GET,RequestMethod.POST);
                    }
                    PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition("/" + StrUtil.toCamelCase(annotation.tableName().toLowerCase()) + "/" + initMethod);
                    if (typeCondition != null) {
                        patternsRequestCondition = typeCondition.combine(patternsRequestCondition);
                    }
                    StaticLog.info("新增：" + patternsRequestCondition.getPatterns() + "请求");
                    RequestMappingInfo requestMappingInfo = new RequestMappingInfo(patternsRequestCondition
                            , requestMethodsRequestCondition, null, null, null, null, null);
                    requestMappingHandlerMapping.registerMapping((RequestMappingInfo) requestMappingInfo, crudAction, getMethod);
                }

            }
       // }
    }

}
