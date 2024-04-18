package com.vox.usercenter.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author VOX
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        //List
        RList<String> list = redissonClient.getList("test-list");
//        list.add("vox");
        String s = list.get(0);
        System.out.println(s);
        list.remove(0);
    }

}
