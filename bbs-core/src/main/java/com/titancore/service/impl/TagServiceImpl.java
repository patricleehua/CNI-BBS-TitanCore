package com.titancore.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.TagDTO;
import com.titancore.domain.entity.Category;
import com.titancore.domain.entity.Tag;
import com.titancore.domain.mapper.TagMapper;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.TagParam;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.TagVo;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.RoleType;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.CategoryService;
import com.titancore.service.TagService;
import com.titancore.util.AuthenticationUtil;
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
    @Autowired
    private CategoryService categoryService;

    @Override
    public DMLVo createTag(TagDTO tagDTO) {
        String userId = tagDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        Tag selectOne = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, tagDTO.getTagName()));
        if(selectOne != null){
            throw new BizException(ResponseCodeEnum.TAG_TAGNAME_IS_EXIST);
        }
        Category category = categoryService.getById(tagDTO.getCategoryId());
        if(category == null){
            throw new BizException(ResponseCodeEnum.CATEGORY_CATEGORYID_IS_NOT_EXIST);
        }
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO,tag);
        tag.setCreatedByUserId(Long.valueOf(tagDTO.getUserId()));
        tag.setCategoryId(category.getId());
        int result = tagMapper.insert(tag);
        DMLVo dmlVo = new DMLVo();
        if(result >0){
            dmlVo.setId(String.valueOf(tag.getId()));
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_CREATE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_CREATE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public List<TagVo> queryTagListByPostId(Long post_id) {
        //todo redis
        List<Tag> tagList = tagMapper.queryTagListByPostId(post_id);
        List<TagVo> tagVoList = tagList.stream().map(tag -> {
            TagVo tagVo = new TagVo();
            BeanUtils.copyProperties(tag, tagVo);
            tagVo.setId(String.valueOf(tag.getId()));
            tagVo.setCategoryId(String.valueOf(tag.getCategoryId()));
            return tagVo;
        }).collect(Collectors.toList());
        return tagVoList;
    }

    @Override
    public PageResult queryList(TagParam tagParam) {
        //todo  redis
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

    @Override
    public DMLVo deleteTagByTagId(String tagId) {
        if(StringUtils.isBlank(tagId)){
            throw new BizException(ResponseCodeEnum.TAG_TAGID_CAN_NOT_BE_NULL);
        }
        Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getId,tagId));
        if(tag == null){
            throw new BizException(ResponseCodeEnum.TAG_TAGID_IS_NOT_EXIST);
        }
        String createByUserId = tag.getCreatedByUserId().toString();
        AuthenticationUtil.checkUserId(createByUserId);
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getId,tagId);
        int result = tagMapper.delete(queryWrapper);
        DMLVo dmlVo = new DMLVo();
        if(result >0){
            dmlVo.setId(tagId);
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_DELETE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_DELETE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public DMLVo updateTagById(TagDTO tagDTO) {
        String userId = tagDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        String tagId = tagDTO.getTagId();
        int result = 0;
        if (StringUtils.isNotBlank(tagId)){
            Tag selectOne = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, tagDTO.getTagName()));
            if(selectOne != null){
                if(!selectOne.getId().toString().equals(tagId)){
                    throw new BizException(ResponseCodeEnum.TAG_TAGNAME_IS_EXIST);
                }
            }
            Tag tag = tagMapper.selectById(tagId);
            if(tag != null){
                LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
                if (StringUtils.isNotEmpty(tagDTO.getTagName())) {
                    updateWrapper.set(Tag::getTagName, tagDTO.getTagName());
                }
                if (StringUtils.isNotEmpty(tagDTO.getTagUrl())) {
                    updateWrapper.set(Tag::getTagUrl, tagDTO.getTagUrl());
                }
                if (StringUtils.isNotEmpty(tagDTO.getDescription())) {
                    updateWrapper.set(Tag::getDescription, tagDTO.getDescription());
                }
                updateWrapper.set(Tag::getUpdateByUserId,userId);
                updateWrapper.eq(Tag::getId, tagId);
                result = tagMapper.update(updateWrapper);
            }
        }else{
            throw new BizException(ResponseCodeEnum.TAG_TAGID_CAN_NOT_BE_NULL);
        }
        DMLVo dmlVo = new DMLVo();
        if(result >0){
            dmlVo.setId(tagId);
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_ERROR);
        }
        return dmlVo;
    }

    private TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        tagVo.setId(String.valueOf(tag.getId()));
        tagVo.setCategoryId(String.valueOf(tag.getCategoryId()));
        return tagVo;
    }
}




