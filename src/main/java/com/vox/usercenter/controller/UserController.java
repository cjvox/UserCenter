package com.vox.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vox.usercenter.common.BaseResponse;
import com.vox.usercenter.common.ErrorCode;
import com.vox.usercenter.common.ResultUtils;

import com.vox.usercenter.constant.UserConstant;
import com.vox.usercenter.exception.BusinessException;
import com.vox.usercenter.pojo.domain.User;
import com.vox.usercenter.pojo.request.SearchUser;
import com.vox.usercenter.pojo.request.UserLoginRequest;
import com.vox.usercenter.pojo.request.UserRegisterRequest;
import com.vox.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static com.vox.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author VOX
 * 用户接口
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;
    /**
     * 接收更新的用户数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> userUpdate(@RequestBody User user,HttpServletRequest request){
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权访问");
        }
        if(user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }
        int i = userService.userUpdate(user);
        return ResultUtils.success(i);

    }

    /**
     * 用于用户自己更改，不可涉及账号、状态、角色、voxcode
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/updateByUser")
    public BaseResponse<Integer> userUpdateMin(@RequestBody User user,HttpServletRequest request){
        if(user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }
        //判断登录信息：
        User loginUser = userService.getLoginUser(request);

//        System.out.println(user.getAvatarUrl());
        int i = userService.userUpdateMin(user,loginUser);
        return ResultUtils.success(i);
    }
    /**
     * 用于用户自己更改头像
     * @return
     */
    @PostMapping("/updateAvatar")
    public BaseResponse<Integer> updateAvatar(@RequestParam("file")MultipartFile photo,@RequestParam("userAccount") String userAccount){
        if(photo==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"头像上传失败");
        }
//        System.out.println(photo.getName());
//        System.out.println(photo.getSize());
//        System.out.println(photo.getContentType());
//        System.out.println(photo.getOriginalFilename());
        String avatarUrl= UserConstant.PhotoPath+UUID.randomUUID()+"."+photo.getOriginalFilename();
        try {
            photo.transferTo(new File(avatarUrl));
            QueryWrapper<User> wrapper=new QueryWrapper<>();
            wrapper.eq("user_account",userAccount);
            User user = userService.getOne(wrapper);
            user.setAvatarUrl(avatarUrl);
            userService.updateById(user);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"服务器存储图片时发生异常");
        }

        return ResultUtils.success(1);

    }

//    @PostMapping("/updateAvatar")
//    public BaseResponse<Integer> updateAvatar(String photo){
//        if(photo==null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"头像上传失败");
//        }
//        System.out.println(photo);
//        return ResultUtils.success(1);
//
//    }


    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest==null){
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String voxCode = userRegisterRequest.getVoxCode();
        if(StringUtils.isAllBlank(userAccount,userPassword,checkPassword,voxCode )){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long id = userService.userRegister(userAccount, userPassword, checkPassword,voxCode );
        return ResultUtils.success(id);
    }
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAllBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码为空");
        }
        User user = userService.doLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }
        int i = userService.userLogout(request);
        return ResultUtils.success(i);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(SearchUser searchuser, HttpServletRequest request){
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权访问");
        }
        List<User> list = userService.SearchUsers(searchuser);
        //流处理，脱敏
        List<User> ResultList = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(ResultList);

    }
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNum, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("voxFriends:user:recommend:%s", loginUser.getId());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Page<User> userList = (Page<User>) valueOperations.get(redisKey);
        //有缓存，直接返回
        if(userList!=null){
            return ResultUtils.success(userList);
        }
        //设置分页，返回page对象
        //无缓存，设置缓存
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userList= userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        try {
            valueOperations.set(redisKey,userList,30000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            log.error("redis set key error");
        }
        return ResultUtils.success(userList);

    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody User user,HttpServletRequest request){
        long id=user.getId();
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权访问");
        }
        if(id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入的id小于0");
        }
        //自动为逻辑删除
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }
    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(Long id,HttpServletRequest request){

        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权访问");
        }
        if(id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入的id小于0");
        }
        //自动为逻辑删除
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }



    /**
     * 获取用户的登录态
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User currentUser= (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录,获取当前用户态失败");
        }
        //现在的currentUser是从session中获取，如果信息频繁变化，
        //返回的对象不准确，所以我们根据id返回
        User safetyUser = userService.getSafetyUser(userService.getById(currentUser.getId()));
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList){
        List<User> users = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(users);
    }

    @GetMapping("/searchTest")
    public BaseResponse<List<User>> searchUserss(SearchUser searchuser, HttpServletRequest request){
        List<User> list = userService.SearchUsers(searchuser);
        //流处理，脱敏
        List<User> ResultList = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(ResultList);
    }
    /**
     *获取最匹配的参数
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request){
        if(num<0||num>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<User> userVOS = userService.matchUsers(num, loginUser);
        return ResultUtils.success(userVOS);
    }
}
