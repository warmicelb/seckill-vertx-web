package com.seckill.vertx.gateway.core;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware {

    public static ApplicationContext APPLICATION_CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }


    public static <T> T getBean(String name) {
        return (T) APPLICATION_CONTEXT.getBean(name);
    }

    public static <T> T getBean(Class<?> className) {
        return (T) APPLICATION_CONTEXT.getBean(className);
    }
}
