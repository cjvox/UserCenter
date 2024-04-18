package com.vox.usercenter.service;

import com.vox.usercenter.pojo.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vox.usercenter.pojo.request.SearchUser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author cjvox
* @description 针对表【user】的数据库操作Service
* @createDate 2024-03-13 17:03:30dd
 *
 * 用户服务
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   账户
     * @param userPassword  密码
     * @param checkPassword 校验码
     * @param voxCode
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String voxCode);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 返回脱敏后的用户信息
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 用用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 更新用户信息
     */
    int userUpdate(User userUpdateRequest);

    int userUpdateMin(User user, User loginUser);

    /**
     * 按条件查询
     */
    List<User> SearchUsers(SearchUser user);

    /**
     * 根据标签搜索用户
     *
     * @param tagList
     * @return
     */
    List<User> searchUserByTags(List<String> tagList);

    /**
     * 获取当前请求的用户信息，和currentUser不一样，这个是
     * 供系统内部使用，currentUser返回的是统一类Base
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    boolean isAdmin(User user);

    /**
     * 匹配用户
     *
     * @param num
     * @param loginUser
     */
    List<User> matchUsers(long num, User loginUser);
}
