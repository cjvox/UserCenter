package com.vox.usercenter.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author VOX
 */
@Data
@AllArgsConstructor
public class TagChildVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7183964093048822747L;

    private String id;
    private String text;
}
