package com.vox.usercenter.service;

import com.vox.usercenter.pojo.domain.Team;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author VOX
 */
@SpringBootTest
public class UpdateTest {

    @Resource
    TeamService teamService;
    @Test
    void test(){
        Team user = teamService.getById(9L);
        //Team(id=9, name=测试更新, description=测试更新, maxNum=4, expireTime=Sat Apr 06 08:00:00 CST 2024, userId=3, status=0, password=, createTime=Fri Apr 05 12:13:42 CST 2024, updateTime=Fri Apr 05 12:13:42 CST 2024, isDelete=0)
        System.out.println(user);
        Team team = new Team();
        team.setId(9L);
//        UpdateWrapper<Team> wrapper=new UpdateWrapper<>();
        team.setName("");
        System.out.println(team);
        BeanUtils.copyProperties(user,team);
        System.out.println(team);
        teamService.updateById(team);
    }
}
