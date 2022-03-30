package net.zzh.dbrest.annotation;

import net.zzh.dbrest.extend.DefaultResultHandler;
import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.extend.ResultHandler;
import net.zzh.dbrest.sql.IdType;

import java.lang.annotation.*;

/**
 * @Description: Crud注解类
 * @author Zeo Zheng
 * @date 2022/1/20 11:58
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbCrud {

    /**
     * 数据库表名，区分大小写
     */
    String tableName();

    /**
     * 数据库表主键名
     */
    String keyField();

    /**
     * 主键类型，如果是uuid会自动生成
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
