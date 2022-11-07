package com.seckill.vertx.gateway.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLocalCache<K,V> {

    /**
     * 缓存容器
     */
    private LoadingCache<K,V> cache;

    private int maxSize;

    private int expireDuration;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 初始化缓存容器与加载规则（定时刷新缓存）
     */
    protected void init(){
        if(cache==null){
            cache = CacheBuilder.newBuilder().maximumSize(maxSize).refreshAfterWrite(expireDuration,timeUnit).build(new CacheLoader<K, V>() {
                @Override
                public V load(K k) throws Exception {
                    return loadData(k);
                }
            });
        }
    }

    /**
     * 加载数据至缓存
     * @param k
     * @return
     */
    public abstract V loadData(K k);

    public V getValue(K k) throws ExecutionException {
        return cache.get(k);
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getExpireDuration() {
        return expireDuration;
    }

    public void setExpireDuration(int expireDuration) {
        this.expireDuration = expireDuration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
