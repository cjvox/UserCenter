package com.vox.usercenter.pojo.request;

import lombok.Data;

/**
 * @author VOX
 */
@Data
public class SearchUser {
    private String username;
    private String userAccount;
    private String gender;
    private String phone;
    private String email;
    private String voxCode;
    private int userRole=-1;
    private int userStatus=-1;
}
