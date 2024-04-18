package com.vox.usercenter.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author VOX
 */
@Data
public class UserLoginRequest implements Serializable{
    @Serial
    private static final long serialVersionUID = 2524177057130684345L;
    private String userAccount, userPassword, checkPassword;
}
