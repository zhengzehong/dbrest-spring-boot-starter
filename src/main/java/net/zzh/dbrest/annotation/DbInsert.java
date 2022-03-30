package net.zzh.dbrest.annotation;


import net.zzh.dbrest.extend.DefaultResultHandler;
import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.extend.ResultHandler;
import net.zzh.dbrest.sql.IdType;

import java.lang.annotation.*;

/**
 * @Description: DbInsert注解类
 * @author Zeo Zheng
 * @date 2022/1/20 11:58
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbInsert {

    /**
     * sql语句
     */
    String value();
    /**
     * 仅支持IdType.NONE和IdType.Auto
     */
    IdType idtype() default IdType.NONE;
    /**
     * 请求结果处理类
     */
    Class<? extends ResultHandler> resultHandler() default DefaultResultHandler.class;
    /**
     * 前置请求处理类
     */
    Class<? extends RequestHandler> requestHandler() default RequestHandler.class;
}