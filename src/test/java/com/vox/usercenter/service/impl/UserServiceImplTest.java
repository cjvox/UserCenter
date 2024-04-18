package com.vox.usercenter.service.impl;

import com.vox.usercenter.pojo.domain.User;
import com.vox.usercenter.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Arrays;
import java.util.List;

/**
 * @author VOX
 */
@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserService userService;
    @Test
    void searchUserByTags() {
        List<String> list= Arrays.asList("java","c");
        List<User> users = userService.searchUserByTags(list);
        Assertions.assertNotNull(users);
    }

}