package com.seckill.vertx.gateway.core;

import com.seckill.vertx.gateway.core.verticle.SecKillVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.*;

/**
 * 启动加载类
 */
@Configuration
@ImportResource("classpath:spring/spring-context.xml")
@ComponentScan("com.seckill.vertx")
public class InitialModule {

    private static final Logger LOGGER = LogManager.getLogger(InitialModule.class);

    @Bean
    @DependsOn({"springContextHolder"})
    public DeploymentOptions deployVertxApplications(VertxOptions vertxOptions, DeploymentOptions deploymentOptions){
        //初始化创建vertx实例
        Vertx vertx = Vertx.vertx(vertxOptions);
        //启动部署vertx实例
        vertx.deployVerticle(SecKillVerticle.class,deploymentOptions,stringAsyncResult -> {
            if(stringAsyncResult.succeeded()){
                LOGGER.info("SecKillVerticle deployed success!");
            }else{
                LOGGER.error("SecKillVerticle deployed Fail.. :"+stringAsyncResult.cause().toString());
            }
        });
        return deploymentOptions;
    }
}
