package com.vox.usercenter.pojo.dto;

import com.vox.usercenter.pojo.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;


/**
 * @author VOX
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {
    @Serial
    private static final long serialVersionUID = -3139139903281779281L;
    /**
     * id
     */
    private Long id;

    /**
     * id列表
     */
    private List<Long> idList;


    /**
     * 队伍名称
     */
    private String name;

    /**
     *关键词，同时搜索队伍名称和描述
     */
    private String searchText;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;


    /**
     * 用户id
     */
    private Long userid;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

}
