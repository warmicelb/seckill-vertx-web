package com.seckill.vertx.gateway.cache.activity;

import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import com.demo.support.export.ActivityExportService;
import com.seckill.vertx.gateway.cache.AbstractLocalCache;
import com.seckill.vertx.gateway.cache.ILocalCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

@Service
public class ActivityLocalCache extends AbstractLocalCache<String, SeckillActivityDTO> implements ILocalCache<String, SeckillActivityDTO> {

    private final static Logger LOGGER = LogManager.getLogger(ActivityLocalCache.class);

    /**
     * 自定义lru缓存容量大小
     */
    @Value("100")
    private int maxSize;

    /**
     * 自定义过期时间
     */
    @Value("1000")
    private int expireDuration;

    @Autowired
    private ActivityExportService activityExportService;


    @PostConstruct
    public void init() {
        setMaxSize(maxSize);
        setExpireDuration(expireDuration);
        super.init();
    }

    @Override
    public SeckillActivityDTO loadData(String s) {
        Result<SeckillActivityDTO> seckillActivityDTOResult = activityExportService.queryActivity(s);
        if (seckillActivityDTOResult != null) {
            return seckillActivityDTOResult.getData();
        }
        return null;
    }


    @Override
    public SeckillActivityDTO get(String key) {
        try {
            return getValue(key);
        } catch (ExecutionException e) {
            LOGGER.error("ActivityLocalCache get error:" + key, e);
        }
        return null;
    }
}
