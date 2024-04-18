package com.vox.usercenter.service;

import com.vox.usercenter.mapper.UserMapper;
import com.vox.usercenter.pojo.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author VOX
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Resource
    UserMapper mapper;
    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("yuxiangpeng");
        user.setUserAccount("");
        user.setAvatarUrl("https://img.zcool.cn/community/01926d5d6a3f8da801211f9e12d2a4.jpg@1280w_1l_2o_100sh.jpg");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("456");
        user.setEmail("789");

        boolean save = userService.save(user);
        System.out.println(user.getId());
        assertTrue(save);
    }

    @Test
    void userRegister() {
        String userAccount="voxLike";
        String usePassword="123123123";
        String checkPassword="123123123";
        String voxCode="2";
        userService.getById(1);
//        List<User> users = mapper.SelectTestAll();
//        System.out.println(users);

//        User user=mapper.getUserByAccountTest("sd");
//        System.out.println(user);

//        User user=mapper.getUserByAccountTestB("sd");
//        System.out.println(user);

//        long l = userService.userRegister(userAccount, usePassword, checkPassword, voxCode);
//        Assertions.assertEquals(-1,l);

    }

    @Test
    void searchUserByTags() {
        List<String> list= Arrays.asList("java","c");
        List<User> users = userService.searchUserByTags(list);
        Assertions.assertNotNull(users);
    }
}