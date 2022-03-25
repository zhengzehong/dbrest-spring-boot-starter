package net.zzh.dbrest.annotation;

import net.zzh.dbrest.DbRestRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DbRestRegistrar.class)
@ConditionalOnWebApplication
public @interface EnableDbRest {

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    String[] value() default {};
}
