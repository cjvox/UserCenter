package com.vox.usercenter.once;

import com.vox.usercenter.mapper.UserMapper;
import com.vox.usercenter.pojo.domain.User;
import jakarta.annotation.Resource;

/**
 * @author VOX
 */
//@Component
public class InsertUsers {
    @Resource
    private UserMapper mapper;

    /**
     * 批量导入用户
     */
//    @Scheduled
    public void doInsertUser(){
        final int NUM=10000000;
        for(int i=0;i<NUM;i++){
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
            mapper.insert(user);
        }
    }

    public static void main(String[] args) {
        new InsertUsers().doInsertUser();
    }
}
