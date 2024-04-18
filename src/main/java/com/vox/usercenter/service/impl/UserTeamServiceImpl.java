package com.vox.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vox.usercenter.mapper.UserTeamMapper;
import com.vox.usercenter.pojo.domain.UserTeam;
import com.vox.usercenter.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author cjvox
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-04-04 12:01:42
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




