package com.vox.usercenter.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.vox.usercenter.pojo.request.UserUpdateRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
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
    private String userPassword;

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
     * 
     */
    @TableLogic
    private Integer isDelete;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}