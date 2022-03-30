package net.zzh.dbrest.spring;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import net.zzh.dbrest.annotation.DbRestController;

import java.lang.annotation.Annotation;

/**
 * @Description: 配置处理类，逻辑处理的入口类
 * @author Zeo Zheng
 * @date 2022/1/23 16:58
 * @version 1.0
 */
public class DbRestScannerConfigure implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, BeanNameAware {

    private String basePackage;
    private Class<? extends Annotation> annotationClass = DbRestController.class;
    private Class<?> markerInterface = DbRestController.class;
    private ApplicationContext applicationContext;
    private String beanName;
    private BeanNameGenerator nameGenerator;

    public DbRestScannerConfigure() {
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = DbRestController.class;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public BeanNameGenerator getNameGenerator() {
        return this.nameGenerator;
    }

    public void setNameGenerator(BeanNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    /**
     * 扫描bean
     * @param registry
     */
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        DbRestScanner scanner = new DbRestScanner(registry);
        scanner.setAnnotationClass(this.annotationClass);
        scanner.setMarkerInterface(this.markerInterface);
        scanner.setResourceLoader(this.applicationContext);
        scanner.setBeanNameGenerator(this.nameGenerator);
        scanner.registerFilters();
        scanner.scan(StringUtils.tokenizeToStringArray(getBasePackage(), ",; \t\n"));
    }

}
