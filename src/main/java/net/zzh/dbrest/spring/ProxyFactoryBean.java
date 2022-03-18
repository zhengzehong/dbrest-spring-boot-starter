package net.zzh.dbrest.spring;


import org.springframework.beans.factory.FactoryBean;

public class ProxyFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    public ProxyFactoryBean() {

    }

    public ProxyFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
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

}

