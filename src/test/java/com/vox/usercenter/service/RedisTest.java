package com.vox.usercenter.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author VOX
 */
@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void text(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("vox","god");
        valueOperations.set("voxNum",1);
        valueOperations.get("vox");
    }
}
