package net.zzh.dbrest.spring;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import net.zzh.dbrest.annotation.DbCrud;
import net.zzh.dbrest.sql.CrudAction;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class ProxyFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    public ProxyFactoryBean() {

    }

    public ProxyFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        this.initCrud();
    }

    public T getObject() throws Exception {
        return DbRestProxyFactory.getProxy(mapperInterface);
    }

    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return this.mapperInterface;
    }

    public void initCrud() {
        Method[] declaredMethods = this.mapperInterface.getDeclaredMethods();
        if (declaredMethods.length > 0) {
            for (Method declaredMethod : declaredMethods) {
                DbCrud annotation = AnnotationUtil.getAnnotation(declaredMethod, DbCrud.class);
                if (annotation != null) {
                    CrudHandler.addCrudMethod(declaredMethod);
                }
            }
        }
    }

}

