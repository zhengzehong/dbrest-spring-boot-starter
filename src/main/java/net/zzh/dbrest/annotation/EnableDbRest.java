package net.zzh.dbrest.annotation;

import net.zzh.dbrest.DbRestRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Description: 用于spring boot启动类，引入初始化相关配置
 * @author Zeo Zheng
 * @date 2022/1/20 11:58
 * @version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DbRestRegistrar.class)
@ConditionalOnWebApplication
public @interface EnableDbRest {
    /**
     * 扫描的basePackage
     */
    String[] basePackages() default {};
    /**
     * 基于class获取basePackage
     */
    Class<?>[] basePackageClasses() default {};
    /**
     * 同扫描的basePackage
     */
    String[] value() default {};
}
