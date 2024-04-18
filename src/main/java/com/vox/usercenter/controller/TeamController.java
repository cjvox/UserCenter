package com.vox.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vox.usercenter.common.BaseResponse;
import com.vox.usercenter.common.ErrorCode;
import com.vox.usercenter.common.ResultUtils;
import com.vox.usercenter.exception.BusinessException;
import com.vox.usercenter.pojo.domain.Team;
import com.vox.usercenter.pojo.domain.User;
import com.vox.usercenter.pojo.domain.UserTeam;
import com.vox.usercenter.pojo.dto.TeamQuery;
import com.vox.usercenter.pojo.request.*;
import com.vox.usercenter.pojo.vo.TeamUserVO;
import com.vox.usercenter.service.TeamService;
import com.vox.usercenter.service.UserService;
import com.vox.usercenter.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author VOX
 */

@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if(teamAddRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入队伍为空");
        }
        User loginUser = userService.getLoginUser(request);
        Team team=new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if(teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入队伍为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);

        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }


    @GetMapping("/get")
    public BaseResponse<Team> getTeam(long id){
        if(id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入的id需要大于0");
        }
        Team team = teamService.getById(id);
        if(team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询结果为空");
        }
        return ResultUtils.success(team);
    }
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> getTeamList(@ParameterObject TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询结果为空");
        }
        boolean admin = userService.isAdmin(request);
        List<TeamUserVO> list = teamService.listTeam(teamQuery,admin);
        //加上加入的标识
        if(!CollectionUtils.isEmpty(list)){
            //1.查询当前加入的人数
            //2.为当前用户加入的队伍设置hasJoin,先查询查出的队伍id
            List<Long> teamIdList = list.stream().map(TeamUserVO::getId).collect(Collectors.toList());
            User loginUser = userService.getLoginUser(request);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.in("team_id",teamIdList);
            //查询出当前要返回的team的user-team表字段
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            //给每个字段赋值hasJoinNum，根据teamId分组然后更改list值
            Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
            list.forEach(team->{
                team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(),new ArrayList<>()).size());
            });

            //找到当前登录用户加入的team
            userTeamQueryWrapper.eq("user_id",loginUser.getId());
            //获取到要返回的列表中，当前用户加入的队伍记录
             userTeamList = userTeamService.list(userTeamQueryWrapper);
            //获取加入的队伍id
            Set<Long> hasJoin = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            list.forEach(team->{
                boolean contain = hasJoin.contains(team.getId());
                team.setHasJoin(contain);
            });
            //查询加入队伍的用户信息（人数）
        }

        return ResultUtils.success(list);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamListByPage(TeamQuery teamQuery){
        if(teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询结果为空");
        }
        Team team=new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> page=new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper=new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean>  joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest request){
        if(teamJoinRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result=teamService.joinTeam(teamJoinRequest,loginUser);
        return ResultUtils.success(result);
    }
    @PostMapping("/quit")
    public BaseResponse<Boolean>  quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if(teamQuitRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result=teamService.quitTeam(teamQuitRequest,loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request){
        Long id = teamDeleteRequest.getTeamId();
        if(id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入的id需要大于0");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeamById(id,loginUser);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取创建的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/leader")
    public BaseResponse<List<TeamUserVO>> getMyTeam(@ParameterObject TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询结果为空");
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserid(loginUser.getId());
        List<TeamUserVO> list = teamService.listTeam(teamQuery,true);
        return ResultUtils.success(list);
    }

    /**
     * 获取加入的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/join")
    public BaseResponse<List<TeamUserVO>> getJoinTeamList(@ParameterObject TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询结果为空");
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id",userId);
        List<UserTeam> idList = userTeamService.list(userTeamQueryWrapper);
        //根据teamID分组，防止有相同的id
        Map<Long, List<UserTeam>> listMap = idList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> idListTrue = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idListTrue);
        List<TeamUserVO> list = teamService.listTeam(teamQuery,true);
        return ResultUtils.success(list);
    }


}
