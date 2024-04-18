package com.vox.usercenter.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 用户退出队伍
 * @author VOX
 */

@Data
public class TeamDeleteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5648128805977351909L;

    private Long teamId;

}
