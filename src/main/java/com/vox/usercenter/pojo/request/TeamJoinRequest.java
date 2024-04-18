package com.vox.usercenter.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * @author VOX
 */

@Data
public class TeamJoinRequest implements Serializable {


    @Serial
    private static final long serialVersionUID = 7966716330501208072L;
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
