package com.seckill.vertx.gateway.core.verticle;

import com.seckill.vertx.gateway.core.router.BusinessRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.concurrent.TimeUnit;

public class SecKillVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        //创建路由（负责映射请求和处理器）
        Router router = Router.router(vertx);

        //1.给router配置通用handler(统一化请求入参，可做校验等)
        configCommonHandler(router);

        //2.配置router-业务URL与处理方法
        new BusinessRouter(router).loadRouter();

        //创建静态资源处理器，并设置开启缓存
        StaticHandler staticHandler = StaticHandler.create().setCachingEnabled(true);
        staticHandler.setWebRoot("seckill-vertx-gateway/src/main/resources");
        //绑定.html请求到静态处理器，ordered指定是否顺序处理（true标识同一个匹配的请求不会被并发处理）
        router.routeWithRegex(".*\\.html").blockingHandler(staticHandler,false);
        router.routeWithRegex(".*\\.jpg").blockingHandler(staticHandler,false);
        router.routeWithRegex(".*\\.js").blockingHandler(staticHandler,false);

        HttpServerOptions serverOptions = new HttpServerOptions();
        //设置连接的空闲超时时间20s（这里上游是nginx服务，所以之类保持长连接）
        serverOptions.setIdleTimeout(20).setIdleTimeoutUnit(TimeUnit.SECONDS);
        //启动服务，绑定路由处理器，设置监听端口和回调处理
        vertx.createHttpServer().requestHandler(router).listen(8080,http->{
            if (http.succeeded()) {
                //启动成功
                startPromise.complete();
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    private void configCommonHandler(Router router) {
        //放入BodyHandler，不管参数在URL上还是在form表单中，都可以通过request.getParam()获取
        router.route().handler(BodyHandler.create());
        // 统一拦截器，比如校验登录态、打印日志等
    }
}
