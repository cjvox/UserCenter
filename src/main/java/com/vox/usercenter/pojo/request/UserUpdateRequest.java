package com.vox.usercenter.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author VOX
 */
@Data
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5597948319067500876L;
    private Long id;
    private int userStatus;
    private int gender;
    private int userRole;
//    private int index;
    private Integer isDelete;
    private String username,userAccount,avatarUrl,phone,email,voxCode,createTime,updateTime,userPassword;
}
