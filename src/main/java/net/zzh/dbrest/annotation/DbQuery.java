package net.zzh.dbrest.annotation;



import net.zzh.dbrest.extend.DefaultResultHandler;
import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.extend.ResultHandler;

import java.lang.annotation.*;

/**
 * @Description: DbQuery注解类
 * @author Zeo Zheng
 * @date 2022/1/20 11:58
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbQuery{

    /**
     * sql语句
     */
    String value();
    /**
     * 请求结果处理类
     */
    Class<? extends ResultHandler> resultHandler() default DefaultResultHandler.class;
    /**
     * 前置请求处理类
     */
    Class<? extends RequestHandler> requestHandler() default RequestHandler.class;

}
