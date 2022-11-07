package com.seckill.vertx.gateway.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 主启动类
 */
public class MainLauncher {

    public static void main(String[] args) {
        //通过spring管理类，这里通过加载InitialModule来启动vertx服务
        new AnnotationConfigApplicationContext(InitialModule.class);
    }
}
