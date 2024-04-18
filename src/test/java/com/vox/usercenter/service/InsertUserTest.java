package com.vox.usercenter.service;

import com.vox.usercenter.mapper.UserMapper;
import com.vox.usercenter.pojo.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author VOX
 */
@SpringBootTest
public class InsertUserTest {
    @Resource
    private UserMapper mapper;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    @Resource
    private UserService userService;
    /**
     * 批量导入用户
     */
    @Test
    public void doInsertUserTest(){
        final int NUM=100000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int j=0;
        int batchSize=5000;
        List<CompletableFuture<Void>> futureList=new ArrayList<>();
        for(int i=0;i<10;i++){
            ArrayList<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setId(0L);
                user.setUsername("fakeVox");
                user.setUserAccount("fakeVoxAccount");
                user.setAvatarUrl("https://img.ixintu.com/download/jpg/20200724/cca207852d756a2609f2b39e34d29806_512_512.jpg!ys");
                user.setGender(0);
                user.setUserPassword("123123123");
                user.setPhone("123123");
                user.setEmail("123123@qq.com");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setVoxCode("999999");
                user.setTags("[]");
                user.setProfile("fake");
                userList.add(user);
                if(j%batchSize==0){
                    break;
                }
            }
            CompletableFuture<Void> future=CompletableFuture.runAsync(()->{
                System.out.println("threadName:"+Thread.currentThread().getName());
                userService.saveBatch(userList,batchSize);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        //还可以自定义线程池
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
