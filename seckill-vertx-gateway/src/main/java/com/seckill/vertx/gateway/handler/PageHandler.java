package com.seckill.vertx.gateway.handler;

import com.seckill.vertx.gateway.core.annotation.RequestMapping;
import com.seckill.vertx.gateway.core.enums.ResponseType;
import org.springframework.stereotype.Component;

@Component
@RequestMapping("/settlement")
public class PageHandler {

    @RequestMapping(value = "/page",rt = ResponseType.HTML)
    public String settle(){
        return "/html/settlement.html";
    }


}
