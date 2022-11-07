package com.seckill.vertx.gateway.cache;

/**
 * 缓存获取通用接口
 * @param <K>
 * @param <V>
 */
public interface ILocalCache<K,V> {

    /**
     * 从本地获取缓存
     * @param key
     * @return
     */
    V get(K key);
}
