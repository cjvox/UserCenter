package com.vox.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vox.usercenter.pojo.domain.Team;
import com.vox.usercenter.pojo.domain.User;
import com.vox.usercenter.pojo.dto.TeamQuery;
import com.vox.usercenter.pojo.request.TeamJoinRequest;
import com.vox.usercenter.pojo.request.TeamQuitRequest;
import com.vox.usercenter.pojo.request.TeamUpdateRequest;
import com.vox.usercenter.pojo.vo.TeamUserVO;

import java.util.List;

/**
* @author cjvox
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-04-04 11:56:07
*/
public interface TeamService extends IService<Team> {
    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param loginUser
     * @return
     */
    List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean loginUser);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeamById(long id, User loginUser);
}
