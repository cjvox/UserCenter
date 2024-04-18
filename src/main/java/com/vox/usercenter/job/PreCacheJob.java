package com.vox.usercenter.job;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vox.usercenter.pojo.domain.User;
import com.vox.usercenter.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author VOX
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;
    //重点用户
    private List<Long> mainUserList= Arrays.asList(1L,2L,3L);

    /**
     * 每天执行，预热推荐用户
     */
    @Scheduled(cron = "0 19 20 * * ? ")
    public void doCacheRecommendUser(){
        //设置分布式锁
        RLock lock = redissonClient.getLock("voxFriends:user:doCache:lock");
        try {
            //第一个参数：等待时间为0，因为是定时任务
            if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS)) {
//                Thread.sleep(30000);
                //定时任务主体
                for(Long userId:mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage=userService.page(new Page<>(1,20),queryWrapper);
                    String redisKey = String.format("voxFriends:user:recommend:%s", userId);
                    ValueOperations valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        log.error("redis set key error");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
