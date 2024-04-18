package com.vox.usercenter.service.impl;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vox.usercenter.common.ErrorCode;
import com.vox.usercenter.exception.BusinessException;
import com.vox.usercenter.mapper.TagMapper;
import com.vox.usercenter.pojo.domain.Tag;
import com.vox.usercenter.pojo.vo.TagChildVo;
import com.vox.usercenter.pojo.vo.TagParentVo;
import com.vox.usercenter.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
* @author cjvox
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2024-04-15 10:25:41
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {


    /**
     * 获取标签
     * @return
     */
    @Override
    public List<TagParentVo> getTags() {
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("is_parent",1);
        List<Tag> Parentlist = this.list(tagQueryWrapper);
        if(CollectionUtils.isEmpty(Parentlist)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        HashMap<Long, TagParentVo> TagParentVoMap = new HashMap<>();
        for(Tag ParentTag:Parentlist){
            Long id = ParentTag.getId();
            String tagName = ParentTag.getTagName();
            TagParentVoMap.put(id,new TagParentVo(tagName,new ArrayList<TagChildVo>()));
        }
        tagQueryWrapper=new QueryWrapper<>();
        tagQueryWrapper.eq("is_parent",0);
        List<Tag> ChildList = this.list(tagQueryWrapper);
        for(Tag childTag:ChildList){
            String tagName = childTag.getTagName();
            Long parentId = childTag.getParentId();
            TagParentVoMap.get(parentId).getChildren().add(new TagChildVo(tagName,tagName));
        }
        List<TagParentVo> result =new ArrayList<>(TagParentVoMap.values());
        return result;
    }
}




