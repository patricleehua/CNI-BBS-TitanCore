package com.titancore.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.TagDTO;
import com.titancore.domain.entity.Tag;
import com.titancore.domain.mapper.TagMapper;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.TagParam;
import com.titancore.domain.vo.DDLVo;
import com.titancore.domain.vo.TagVo;
import com.titancore.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public DDLVo createTag(TagDTO tagDTO) {
        //todo 创建者须与当前登入用户一致

        //todo 异常处理 优化代码

        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO,tag);

        int result = tagMapper.insert(tag);
        DDLVo ddlVo = new DDLVo();
        if(result >0){
            ddlVo.setId(String.valueOf(tag.getId()));
            ddlVo.setStatus(true);
            ddlVo.setMessage("插入成功");
        }else{
            ddlVo.setStatus(false);
            ddlVo.setMessage("插入失败");
        }
        return ddlVo;
    }

    @Override
    public List<TagVo> queryTagListByPostId(Long post_id) {
        //todo redis
        List<Tag> tagList = tagMapper.queryTagListByPostId(post_id);
        List<TagVo> tagVoList = tagList.stream().map(tag -> {
            TagVo tagVo = new TagVo();
            BeanUtils.copyProperties(tag, tagVo);
            return tagVo;
        }).collect(Collectors.toList());
        return tagVoList;
    }

    @Override
    public PageResult queryList(TagParam tagParam) {
        //todo 异常处理 redis
        Page<Tag> page = new Page<>(tagParam.getPageNo(),tagParam.getPageSize());
        LambdaQueryWrapper<Tag> queryWrapper =  new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(tagParam.getTagName())){
            queryWrapper.eq(Tag::getTagName,tagParam.getTagName());
        }
        Page<Tag> tagPage = tagMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(pageResult.getTotal());
        pageResult.setRecords(tagPage.getRecords().stream().map(this::copy).toList());
        return pageResult;
    }

    private TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
}




