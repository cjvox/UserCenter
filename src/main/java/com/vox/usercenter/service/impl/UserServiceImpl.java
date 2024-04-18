package com.vox.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vox.usercenter.common.ErrorCode;
import com.vox.usercenter.exception.BusinessException;
import com.vox.usercenter.pojo.domain.User;
import com.vox.usercenter.mapper.UserMapper;
import com.vox.usercenter.pojo.request.SearchUser;
import com.vox.usercenter.service.UserService;
import com.vox.usercenter.utils.AlgorithmUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.marshalling.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.vox.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.vox.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author cjvox
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-03-13 17:03:30
 *
 * 用户服务实现类
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    private static final String SALT="voxLoveCoding";


    @Autowired(required = false)
    private UserMapper mapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String voxCode) {
        //0.校验是否为空
        if(StringUtils.isAllBlank(userAccount,userPassword,checkPassword,voxCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //1.校验特殊编码voxCode
        if(voxCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"voxCode为空");
        }
        //2.校验账户
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }
        //3.校验密码
        if(userPassword.length()<8||checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过长");
        }

        //4.账户不能包含特殊字符
        String regEx="[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        //5.账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        //设置条件
        userQueryWrapper.eq("user_account",userAccount);
        long count = mapper.selectCount(userQueryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        //6.voxCode
        userQueryWrapper = new QueryWrapper<>();
        //设置条件
        userQueryWrapper.eq("vox_code",voxCode);
        count = mapper.selectCount(userQueryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该voxCode已经使用");
        }
        //6.密码加密

        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        //7.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setVoxCode(voxCode);
        user.setUsername("User");
        boolean res = this.save(user);
        if(!res){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"存储失败");
        }
        return user.getId();
    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验是否为空
        if(StringUtils.isAllBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有传递账号或密码");
        }
        //2.校验账户
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号小于4位");
        }
        //3.校验密码
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码小于8位");
        }

        //4.账户不能包含特殊字符
        String regEx="[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }

        //是否存在：
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account",userAccount);
        long count = mapper.selectCount(userQueryWrapper);
        if(count<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不存在,请注册");
        }
        //密码匹配
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        userQueryWrapper.eq("user_password",encryptPassword);
        User user = mapper.selectOne(userQueryWrapper);
        //用户不存在
        if(user==null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码错误");
        }
        //用户脱敏
        User safeUser=getSafetyUser(user);
        //记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safeUser);

        //可以加入限流功能，记录登录次数等限制登录
        return safeUser;
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user){
        if(user==null){
            return null;
        }
        User safeUser=new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setVoxCode(user.getVoxCode());
        safeUser.setTags(user.getTags());
        safeUser.setProfile(user.getProfile());
        return safeUser;
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
    /**
     * 用户更新
     */
    @Override
    public int userUpdate(User user) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id",user.getId());
        User userOld = mapper.selectOne(userQueryWrapper);
        if (userOld==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"当前用户不存在");
        }
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account",user.getUserAccount());
        User test=mapper.selectOne(userQueryWrapper);
        if(test!=null&&test.getId()!=userOld.getId()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号名已存在");
        }
        user.setUserPassword(userOld.getUserPassword());
        user.setVoxCode(userOld.getVoxCode());
        user.setIsDelete(userOld.getIsDelete());
        user.setUpdateTime(new Date());
        mapper.updateById(user);
        return 1;

    }
    @Override
    public int userUpdateMin(User user, User loginUser) {
        String username = user.getUsername();
        Integer gender = user.getGender();
        String userPassword = user.getUserPassword();
        String phone = user.getPhone();
        String email = user.getEmail();
        String profile = user.getProfile();
        if(!user.getUserAccount().equals(loginUser.getUserAccount())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"无法执行更改操作");
        }
        //非法操作则抛出异常
        if(gender==null||username==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名和性别不可以为空！！！");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account",user.getUserAccount());
        User userOld = mapper.selectOne(userQueryWrapper);
        if(!username.equals(userOld.getUsername())){
            userOld.setUsername(username);
        }
        if(!gender.equals(userOld.getGender())){
            userOld.setGender(gender);
        }
        if(userPassword!=null&&!userPassword.equals(userOld.getUserPassword())){
            if(userPassword.length()<8){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不可小于8");
            }
            String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
            userOld.setUserPassword(encryptPassword);
        }
        if(userOld.getPhone()==null||!userOld.getPhone().equals(phone)){
            userOld.setPhone(phone);
        }
        if(userOld.getEmail()==null||!userOld.getEmail().equals(email)){
            userOld.setEmail(email);
        }
        if(userOld.getProfile()==null||!userOld.getProfile().equals(profile)){
            userOld.setProfile(profile);
        }
        mapper.updateById(userOld);
        return 1;

    }
    /**
     * 查询函数
     */
    public List<User> SearchUsers(SearchUser user){
         String username = user.getUsername();
         String userAccount = user.getUserAccount();
         String gender = user.getGender();
         String phone = user.getPhone();
         String email = user.getEmail();
         String voxCode = user.getVoxCode();
         int userRole = user.getUserRole();
         int userStatus=user.getUserStatus();
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        if(username!=null){
            queryWrapper.eq("username",username);
        }
        if(userAccount!=null){
            queryWrapper.eq("user_account",userAccount);
        }
        if(gender!=null){
            queryWrapper.eq("gender",gender);
        }
        if(phone!=null){
            queryWrapper.eq("phone",phone);
        }
        if(email!=null){
            queryWrapper.eq("email",email);
        }
        if(voxCode!=null){
            queryWrapper.eq("vox_code",voxCode);
        }
        if(userRole!=-1){
            queryWrapper.eq("user_role",userRole);
        }
        if(userStatus!=-1){
            queryWrapper.eq("user_status",userStatus);
        }
        List<User> users = mapper.selectList(queryWrapper);
        return users;
    }

    /**
     * 根据标签搜索用户
     *使用sql版
     * @param tagList
     * @return
     */
    @Deprecated
    private List<User> searchUserByTagsBySQL(List<String> tagList){
        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"标签为空");
        }
//        使用sql来查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //like "%java%"
        for(String tagName:tagList){
            queryWrapper.like("tags",tagName);
        }
        List<User> users = mapper.selectList(queryWrapper);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
    /**
     * 根据标签搜索用户
     *使用内存查询
     * @param tagList
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagList){
        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"标签为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> users = mapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //查询完所有用户，然后再判断包含的标签
        return users.stream().filter(user -> {
            String tag=user.getTags();
//            if(tag==null){
//                return false;
//            }
            Set<String> tags = gson.fromJson(tag, new TypeToken<Set<String>>() {}.getType());
            //使用optional语法判断是否为空
            tags= Optional.ofNullable(tags).orElse(new HashSet<>());
            //and匹配
            for(String tagName:tagList){
                if(!tags.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 获取当前请求的用户信息，和currentUser不一样，这个是
     * 供系统内部使用，currentUser返回的是统一类Base
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request==null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录，没有权限");
        }
        return (User)userObj;
    }
    /**
     * 是否为管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request){
        //管理员可查询。
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) userObj;
        if(user==null||user.getUserRole()!=ADMIN_ROLE){
            return false;
        }
        return true;
    }
    /**
     * 是否为管理员
     * @param
     * @return
     */
    public boolean isAdmin(User user){
        if(user==null||user.getUserRole()!=ADMIN_ROLE){
            return false;
        }
        return true;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("id","tags");
        userQueryWrapper.isNotNull("tags");
        List<User> userList = this.list(userQueryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        List<Pair<User,Long>>listPair=new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            if (StringUtils.isBlank(userTags)|| user.getId().equals(loginUser.getId())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            long distance = AlgorithmUtils.minDistanceTags(userTagList, tagList);
            listPair.add(new Pair<>(user,distance));
        }
        //排序
        List<Pair<User, Long>> collect = listPair.stream()
                .sorted((a, b) -> (int) (a.getB() - b.getB()))
                .limit(num)
                .toList();
        //获取id列表
        List<Long> orderList=collect.stream()
                .map(pair->pair.getA().getId())
                .collect(Collectors.toList());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",orderList);
        //根据id绑定用户、脱敏
        Map<Long, List<User>> userIdListMap = this.list(queryWrapper).
                stream().map(this::getSafetyUser).
                collect(Collectors.groupingBy(User::getId));
        List<User>resultList =new ArrayList<>();
        //按照顺序读取然后返回
        for(Long userId:orderList){
            resultList.add(userIdListMap.get(userId).get(0));
        }
        return resultList;
    }
}




