package net.zzh.dbrest;

import net.zzh.dbrest.annotation.EnableDbRest;
import net.zzh.dbrest.spring.CrudHandler;
import net.zzh.dbrest.spring.DbRestScannerConfigure;
import net.zzh.dbrest.spring.SpringContextHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DbRestRegistrar implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    private Environment environment;

    private DbRestPropertis dbRestPropertis;

    /**
     * 载入配置
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        String basePackge = environment.getProperty("spring.dbrest.base-package");
        String requestHandler = environment.getProperty("spring.dbrest.request-handler");
        String resultHandler = environment.getProperty("spring.dbrest.result-handler");
        String enabled = environment.getProperty("spring.dbrest.enabled");
        String showSql = environment.getProperty("spring.dbrest.showSql");
        this.dbRestPropertis = new DbRestPropertis(basePackge, requestHandler, resultHandler, !"false".equals(enabled), !"false".equals(showSql));
        this.environment = environment;
        DbRestPropertisHolder.setDbRestPropertis(dbRestPropertis);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        if (DbRestPropertisHolder.getDbRestPropertis().getEnabled()) {
            //注册spring bean持有
            BeanDefinitionBuilder springContextHolderBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringContextHolder.class);
            registry.registerBeanDefinition("springContextHolder", springContextHolderBuilder.getBeanDefinition());
            //注册扫描器
            AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableDbRest.class.getName()));
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DbRestScannerConfigure.class);
            List<String> basePackages = new ArrayList();
            basePackages.addAll((Collection) Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));
            basePackages.addAll((Collection)Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));
            basePackages.addAll((Collection)Arrays.stream(annoAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName).collect(Collectors.toList()));
            if (!StringUtils.isEmpty(dbRestPropertis.getBasePackage())) {
                basePackages.add(dbRestPropertis.getBasePackage());
            }
            if (basePackages.isEmpty()) {
                basePackages.add(ClassUtils.getPackageName(annotationMetadata.getClassName()));
            }
            builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
            registry.registerBeanDefinition(generateBaseBeanName(annotationMetadata, 0), builder.getBeanDefinition());
            //注册crudHandler
            BeanDefinitionBuilder crudHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(CrudHandler.class);
            registry.registerBeanDefinition("crudHandler", crudHandlerBuilder.getBeanDefinition());
        }
    }

    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
        return importingClassMetadata.getClassName() + "#" + DbRestScannerConfigure.class.getSimpleName() + "#" + index;
    }
}
