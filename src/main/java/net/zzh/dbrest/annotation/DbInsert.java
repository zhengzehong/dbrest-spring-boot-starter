package net.zzh.dbrest.annotation;


import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.extend.ResultHandler;
import net.zzh.dbrest.sql.IdType;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface DbInsert {

    String value() default "";

    String group() default "";

    Class<? extends ResultHandler> resultHandler() default ResultHandler.class;

    Class<? extends RequestHandler> requestHandler() default RequestHandler.class;

    IdType idtype() default IdType.NONE;

}