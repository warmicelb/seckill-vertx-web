package com.seckill.vertx.gateway.core.router;

import com.alibaba.fastjson.JSON;
import com.seckill.vertx.gateway.core.SpringContextHolder;
import com.seckill.vertx.gateway.core.annotation.RequestMapping;
import com.seckill.vertx.gateway.core.enums.ResponseType;
import com.seckill.vertx.gateway.core.exception.BizException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class BusinessRouter {

    private static final Logger LOGGER = LogManager.getLogger(BusinessRouter.class);

    private Router router;

    public BusinessRouter(Router router) {
        this.router = router;
    }

    /**
     * 加载路由
     */
    public void loadRouter() {
        //根据注解获取映射的处理类
        SpringContextHolder.APPLICATION_CONTEXT.getBeansWithAnnotation(RequestMapping.class).values().forEach(this::registerHandler);
    }

    /**
     * 注册handler映射关系
     *
     * @param handler
     */
    private void registerHandler(Object handler) {
        RequestMapping superAnnotation = handler.getClass().getDeclaredAnnotation(RequestMapping.class);
        String prefixUrl = superAnnotation.value();
        Arrays.stream(handler.getClass().getDeclaredMethods()).filter(f -> f.getAnnotation(RequestMapping.class) != null).forEach(m -> registerMapping(handler, prefixUrl, m));
    }

    private void registerMapping(Object handler, String prefixUrl, Method method) {
        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        //映射的url
        String urlPath = prefixUrl + annotation.value();
        LOGGER.info("begin to register mapping url:"+urlPath);
        //路由注册url处理方法，blockingHandler方法表示交由work线程池来处理任务
        router.route(urlPath).blockingHandler(routingContext->{
            //这里反射调用具体的处理方法，可以在调用逻辑前后做一些自定义拦截操作（监控，限流等）
            try{
                Object result = doInvoke(handler, method,routingContext);
                responseCommon(routingContext,result,annotation);
            } catch (Exception e) {
                LOGGER.error("registerMapping handler call error",e);
                responseException(routingContext,annotation);
            }
        },false);
    }

    private void responseException(RoutingContext context,RequestMapping annotation){
        ResponseType rt = annotation.rt();
        switch (rt){
            case JSON:
                context.response().putHeader("content-type", "application/json").end("{'msg':'内部服务错误','errorCode':'500'}");
                break;
            case HTML:
            default:
                context.reroute("/html/error.html");
                break;
        }
    }

    /**
     * 返回结果处理
     * @param context
     * @param result
     * @param annotation
     */
    private void responseCommon(RoutingContext context, Object result, RequestMapping annotation) throws BizException {
        ResponseType rt = annotation.rt();
        switch (rt){
            case JSON:
                context.response().putHeader("content-type", "application/json").end(JSON.toJSONString(result));
                break;
            case HTML:
                if(!(result instanceof String)){
                    throw new BizException("HTML response type not match for result");
                }
                context.reroute((String) result);
                break;
            default:
                throw new BizException("unsupported response type not match for result");
        }
    }

    /**
     * 执行具体mapping方法
     * @param handler
     * @param method
     * @param context
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object doInvoke(Object handler, Method method, RoutingContext context) throws IllegalAccessException, InvocationTargetException, BizException {
        Parameter[] parameters = method.getParameters();
        if(parameters==null||parameters.length==0){
            //无参数，直接调用
            return method.invoke(handler);
        }
        if(parameters.length==1){
            Parameter parameter = parameters[0];
            if(RoutingContext.class==parameter.getType()){
                return method.invoke(handler,context);
            }
            else if(HttpServerRequest.class==parameter.getType()){
                return method.invoke(handler,context.request());
            }else{
                throw new BizException("unsupported request parameter type");
            }
        }else{
            throw new BizException("unsupported multiply mapping parameters,only one can be registered!");
        }
    }
}
