package com.vox.usercenter.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author VOX
 */
@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -474524027482038975L;

    private String userAccount, userPassword, checkPassword,voxCode;

}
