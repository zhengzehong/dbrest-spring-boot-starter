package net.zzh.dbrest.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbCrud {
    String value() default "";
    String tableName();
}
