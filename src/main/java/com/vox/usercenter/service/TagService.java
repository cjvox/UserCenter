package com.vox.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vox.usercenter.pojo.domain.Tag;
import com.vox.usercenter.pojo.vo.TagParentVo;

import java.util.List;

/**
* @author cjvox
* @description 针对表【tag】的数据库操作Service
* @createDate 2024-04-15 10:25:41
*/
public interface TagService extends IService<Tag> {
    /**
     * 获取标签，按照前端接收的格式来规定
     */
    List<TagParentVo> getTags();
}
