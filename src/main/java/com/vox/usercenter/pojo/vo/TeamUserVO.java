package com.vox.usercenter.pojo.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author VOX
 * 队伍和用户信息封装类
 */
@Data
public class TeamUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6847771356771737624L;
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 创建人
     */
    UserVO createUser;

    /**
     * 是否加入
     */
    private Boolean hasJoin=false;

    /**
     * 已加入的用户数
     */
    private Integer hasJoinNum;

}
