package com.vox.usercenter.pojo.vo;


import lombok.Data;

import java.util.Date;

/**
 * @author VOX
 * 用户包装类
 */
@Data
public class UserVO {
    private Long id;

    /**
     *
     */
    private String username;

    /**
     *
     */
    private String userAccount;

    /**
     *
     */
    private String avatarUrl;

    /**
     *
     */
    private Integer gender;

    /**
     *
     */
    private String phone;

    /**
     *
     */
    private String email;

    /**
     *
     */
    private Integer userStatus;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;


    /**
     * 0:cmmom 1:manager
     */
    private Integer userRole;

    /**
     * 编号
     */
    private String voxCode;
    /**
     * 标签列表json
     */
    private String tags;

    private String profile;

}
