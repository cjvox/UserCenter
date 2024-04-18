package com.vox.usercenter.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 用户退出队伍
 * @author VOX
 */

@Data
public class TeamQuitRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7514511711858271065L;

    private Long teamId;

}
