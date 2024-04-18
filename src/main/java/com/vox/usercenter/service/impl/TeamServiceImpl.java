package com.vox.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vox.usercenter.common.ErrorCode;
import com.vox.usercenter.exception.BusinessException;
import com.vox.usercenter.mapper.TeamMapper;
import com.vox.usercenter.pojo.domain.Team;
import com.vox.usercenter.pojo.domain.User;
import com.vox.usercenter.pojo.domain.UserTeam;
import com.vox.usercenter.pojo.dto.TeamQuery;
import com.vox.usercenter.pojo.enums.TeamStatusEnum;
import com.vox.usercenter.pojo.request.TeamJoinRequest;
import com.vox.usercenter.pojo.request.TeamQuitRequest;
import com.vox.usercenter.pojo.request.TeamUpdateRequest;
import com.vox.usercenter.pojo.vo.TeamUserVO;
import com.vox.usercenter.pojo.vo.UserVO;
import com.vox.usercenter.service.TeamService;
import com.vox.usercenter.service.UserService;
import com.vox.usercenter.service.UserTeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author cjvox
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-04-04 11:56:07
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if(team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(loginUser==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户未登录");
        }
        int maxNum= Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum<1||maxNum>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不符合要求");
        }
        String name = team.getName();
        if(StringUtils.isBlank(name)||name.length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称不符合要求");
        }
        String description = team.getDescription();
        if(StringUtils.isNoneBlank(description)&&description.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
        if(enumByValue==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不符合要求");
        }
        String password = team.getPassword();
        if(TeamStatusEnum.SECRET.equals(enumByValue)){
            if(StringUtils.isBlank(password)||password.length()>32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不符合要求");
            }
        }
        //判断超时时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已超时");
        }

        QueryWrapper<Team> queryWrapper=new QueryWrapper<>();
        long userId = loginUser.getId();
        queryWrapper.eq("user_id",userId );
        long count = this.count(queryWrapper);
        if(count>=5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建5个队伍");
        }
        team.setId(null);
        team.setUserId(userId);

        //事务
        boolean save = this.save(team);
        Long teamId = team.getId();
        if(!save||teamId==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        save = userTeamService.save(userTeam);
        if(!save){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }

        return teamId;

    }

    @Override
    public List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean admin) {
        QueryWrapper<Team> queryWrapper=new QueryWrapper<>();
        //组合查询条件
        if(teamQuery!=null){
            Long id = teamQuery.getId();
            if(id!=null&&id>0){
                queryWrapper.eq("id",id);
            }
            List<Long> idList = teamQuery.getIdList();
            if(!CollectionUtils.isEmpty(idList)){
                queryWrapper.in("id",idList);
            }

            String searchText = teamQuery.getSearchText();
            if(StringUtils.isNoneBlank(searchText)){
                //直接eq自动添加and，所以要手动写or
                queryWrapper.and(qw->qw.like("name",searchText).or().like("description",searchText));
            }
            String name = teamQuery.getName();

            if(StringUtils.isNoneBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if(StringUtils.isNoneBlank(description)){
                queryWrapper.like("description",description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if(maxNum!=null&&maxNum>0){
                queryWrapper.eq("max_num",maxNum);
            }
            //根据创建人查询
            Long userid = teamQuery.getUserid();
            if(userid!=null&&userid>0){
                queryWrapper.eq("user_id",userid);
            }
            //根据状态：
            //Optional.ofNullable(teamQuery.getStatus()).orElse(0);
            Integer status =teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if(statusEnum!=null){
//                statusEnum=TeamStatusEnum.PUBLIC;
//                status=0;
                if(!admin && statusEnum.equals(TeamStatusEnum.PRIVATE)){
                    throw new BusinessException(ErrorCode.NO_AUTH);
                }
                queryWrapper.eq("status",status);
            }

        }
        //过滤超时的队伍
        queryWrapper.and(qw->qw.gt("expire_time",new Date()).or().isNull("expire_time"));
        List<Team> list = this.list(queryWrapper);

        if(CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        //关联查询
        //1.使用sql:
        //select * from team t left join user u on t.userid=u.id
        // select *
        // from team t
        //         left join user_team ut on t.id = ut.teamId
        //         left join user u on ut.userId = u.id;
        //2.mapper、service
        List<TeamUserVO> teamUserVOList=new ArrayList<>();
        for(Team team:list){
            Long userId = team.getUserId();
            if(userId==null){
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            if(user!=null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {

        if(teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if(id==null||id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        Team oldTeam = this.getById(id);
        if(oldTeam==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if(oldTeam.getUserId()!=loginUser.getId()&&!userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        String name = teamUpdateRequest.getName();
        String description = teamUpdateRequest.getDescription();
        Date expireTime = teamUpdateRequest.getExpireTime();
        Integer status = teamUpdateRequest.getStatus();
        String password = teamUpdateRequest.getPassword();
        //如果状态改为加密，必须要有密码
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(statusEnum.equals(TeamStatusEnum.SECRET)){
            if(StringUtils.isNoneBlank(password)){
                oldTeam.setPassword(password);
                oldTeam.setStatus(2);
            }
            if(StringUtils.isAllBlank(password,oldTeam.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"必须要有密码");
            }
        }else if(statusEnum.equals(TeamStatusEnum.PUBLIC)){
            oldTeam.setStatus(0);
        }else if(statusEnum.equals(TeamStatusEnum.PRIVATE)){
            oldTeam.setStatus(1);
        }

        if(StringUtils.isNoneBlank(name)){
            oldTeam.setName(name);
        }
        if(StringUtils.isNoneBlank(description)){
            oldTeam.setDescription(description);
        }
        if(expireTime!=null){
            oldTeam.setExpireTime(expireTime);
        }
        Team team = new Team();
        team.setId(8L);
        boolean result = this.updateById(oldTeam);
//        boolean b = this.updateById(team);
        return result;
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if(teamJoinRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if(expireTime !=null&& expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(TeamStatusEnum.PRIVATE.equals(statusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入该队伍");
        }
        String password = teamJoinRequest.getPassword();
        if(TeamStatusEnum.SECRET.equals(statusEnum)){
            if(StringUtils.isBlank(password)||!password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }
        //分布式锁
        RLock lock = redissonClient.getLock("voxFriends:join_team");
        try {
            //抢锁并执行
            while (true) {
                if(lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                    Long userId = loginUser.getId();
                    //该用户加入队伍的数量
                    QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("user_id",userId);
                    long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
                    if(hasJoinNum>5){
                        throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建5和加入个队伍");
                    }
                    //不能重复加入
                    userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("team_id", teamId);
                    userTeamQueryWrapper.eq("user_id", userId);
                    long userJoinNum = userTeamService.count(userTeamQueryWrapper);
                    if (userJoinNum > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入同一个队伍");
                    }
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    boolean save = userTeamService.save(userTeam);
                    return save;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }finally {
            //释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if(teamQuitRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        Long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper=new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper);
        if(count==0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入队伍");
        }
        long teamHasJoinNum = countTeamUserByTeamId(teamId);
        if(teamHasJoinNum==1){
            //解散队伍
            this.removeById(teamId);
//            return userTeamService.remove(new QueryWrapper<UserTeam>().eq("team_id", teamId));
        }else{
            if(team.getUserId().equals(userId)){
                //队长转移权限
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("team_id",teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if(CollectionUtils.isEmpty(userTeamList)||userTeamList.size()<=1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextLeaderId = nextUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextLeaderId);
                boolean result = this.updateById(updateTeam);
                if(!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
            }
        }
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeamById(long id, User loginUser) {
        Team team = getTeamById(id);
        if(!team.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //移除信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        Long teamId = team.getId();
        queryWrapper.eq("team_id",teamId);
        boolean result = userTeamService.remove(queryWrapper);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        result = this.removeById(teamId);
        return result;
    }

    private Team getTeamById(Long teamId) {
        if(teamId ==null|| teamId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if(team==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return team;
    }

    /**
     * 获取某队当前人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(Long teamId){
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id",teamId);
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        return teamHasJoinNum;
    }
}




