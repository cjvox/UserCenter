package com.vox.usercenter.pojo.vo;

import lombok.Data;

/**
 * @author VOX
 */
@Data
public class TagVo {
    /**
     *
     */
    private Long id;

    /**
     *
     */
    private String tagName;


    /**
     *
     */
    private Long parentId;

    /**
     *
     */
    private Integer isParent;

}
