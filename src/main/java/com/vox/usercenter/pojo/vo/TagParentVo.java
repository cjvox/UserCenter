package com.vox.usercenter.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author VOX
 */
@Data
@AllArgsConstructor
public class TagParentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 6446783825475510226L;

    private String text;
    private List<TagChildVo> children;


}
