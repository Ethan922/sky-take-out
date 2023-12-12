package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShopStatusTask {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String key="SHOP_STATUS";
    /**
     * 每天凌晨1点自动将店铺状态改为打烊
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoCloseShop(){
        log.info("凌晨一点自动将店铺状态设为打烊");
        redisTemplate.opsForValue().set(key,0);
    }
}
