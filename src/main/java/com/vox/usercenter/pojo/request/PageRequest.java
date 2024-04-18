package com.vox.usercenter.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author VOX
 * 通用的分页请求参数
 */
@Data
public class PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -7797970179890314032L;
    /**
     * 页面大小
     */
    protected int pageSize=10;
    /**
     * 当前第几页
     */
    protected int pageNum=1;
}
