package com.vox.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vox.usercenter.common.BaseResponse;
import com.vox.usercenter.common.ErrorCode;
import com.vox.usercenter.common.ResultUtils;
import com.vox.usercenter.exception.BusinessException;
import com.vox.usercenter.pojo.domain.Tag;
import com.vox.usercenter.pojo.vo.TagParentVo;
import com.vox.usercenter.pojo.vo.TagVo;
import com.vox.usercenter.service.TagService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author VOX
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    @Resource
    TagService tagService;

    @GetMapping("/getTags")
    public BaseResponse<List<TagParentVo>> getTags(HttpServletRequest request){
        List<TagParentVo> tags = tagService.getTags();
        if(CollectionUtils.isEmpty(tags)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(tags);
    }

}
