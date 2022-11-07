package com.seckill.vertx.gateway.limit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.seckill.vertx.gateway.core.InitialModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class RateLimitComponent {

    private static final Logger LOGGER = LogManager.getLogger(InitialModule.class);

    /**
     * 接口限流的map
     */
    Cache<String, RateLimiter> RATE_LIMIT_MAP = CacheBuilder.newBuilder().maximumSize(100).build();

    /**
     * 判断接口是否已被限流
     * @param key
     * @return
     */
    public boolean isLimited(String key,double perSeconds){
        try {
            RateLimiter rateLimiter = RATE_LIMIT_MAP.get(key, () -> RateLimiter.create(perSeconds));
            return acquireLimit(rateLimiter,perSeconds);
        } catch (ExecutionException e) {
            LOGGER.error("RateLimitComponent isLimited error",e);
            return false;
        }
    }

    /**
     * 限流频次更新，并获取判断是否被限流
     * @param rateLimiter
     * @param perSeconds
     * @return
     */
    private boolean acquireLimit(RateLimiter rateLimiter, double perSeconds) {
        if(rateLimiter.getRate()!=perSeconds){
            rateLimiter.setRate(perSeconds);
        }
        return rateLimiter.tryAcquire();
    }
}
