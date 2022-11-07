package com.seckill.vertx.gateway.core.annotation;

import com.seckill.vertx.gateway.core.enums.ResponseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    /**
     * 匹配url
     * @return
     */
    String value() default "";

    /**
     * 返回数据类型
     * @return
     */
    ResponseType rt() default ResponseType.JSON;

    /**
     * 是否异步
     * @return
     */
    boolean async() default false;

}
