package net.zzh.dbrest.annotation;

import net.zzh.dbrest.sql.IdType;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbCrud {

    String value() default "";

    String tableName();

    String keyField();

    String orderBy() default "";

    IdType idtype() default IdType.NONE;
}
