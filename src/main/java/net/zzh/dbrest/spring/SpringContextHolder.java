package net.zzh.dbrest.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        assertContextInjected();
        try {
            return applicationContext.getBean(type);
        } catch (Exception e) {
            return null;
        }
    }

    public static void assertContextInjected() {
        if (applicationContext == null) {
            throw new RuntimeException("applicationContext未注入");
        }
    }
}
