package com.titancore.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.CategoryDTO;
import com.titancore.domain.entity.Category;
import com.titancore.domain.mapper.CategoryMapper;
import com.titancore.domain.param.CategoryParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.CategoryVo;
import com.titancore.domain.vo.DMLVo;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.RoleType;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.CategoryService;
import com.titancore.util.AuthenticationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryVo queryCategoryById(Long id) {
        //todo redis
        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getId, id));
        return this.copy(category);
    }

    @Override
    public DMLVo createCategory(CategoryDTO categoryDTO) {
        String userId = categoryDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        Category selectOne = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getCategoryName, categoryDTO.getCategoryName()));
        if(selectOne != null){
            throw new BizException(ResponseCodeEnum.CATEGORY_CATEGORYNAME_IS_EXIST);
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setCreatedByUserId(Long.valueOf(categoryDTO.getUserId()));
        int result = categoryMapper.insert(category);
        DMLVo dmlVo = new DMLVo();
        if(result >0){
            dmlVo.setId(String.valueOf(category.getId()));
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_CREATE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_CREATE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public PageResult queryList(CategoryParam categoryParam) {
        //todo  redis
        Page<Category> page = new Page<>(categoryParam.getPageNo(),categoryParam.getPageSize());
        LambdaQueryWrapper<Category> queryWrapper =  new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(categoryParam.getCategoryName())){
            queryWrapper.eq(Category::getCategoryName,categoryParam.getCategoryName());
        }
        Page<Category> categoryPage = categoryMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(categoryPage.getTotal());
        pageResult.setRecords(categoryPage.getRecords().stream().map(this::copy).toList());
        return pageResult;
    }

    @Override
    public DMLVo deleteCategoryByCategoryId(String categoryId) {

        if (StringUtils.isEmpty(categoryId)){
            throw new BizException(ResponseCodeEnum.CATEGORY_CATEGORYID_CAN_NOT_BE_NULL);
        }

        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getId,categoryId));
        if(category == null){
            throw new BizException(ResponseCodeEnum.CATEGORY_CATEGORYID_IS_NOT_EXIST);
        }
        String createByUserId = category.getCreatedByUserId().toString();
        AuthenticationUtil.checkUserId(createByUserId);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getId,categoryId);
        int result = categoryMapper.delete(queryWrapper);
        DMLVo dmlVo = new DMLVo();
        if(result >0){
            dmlVo.setId(categoryId);
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_DELETE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_DELETE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public DMLVo updateCategoryById(CategoryDTO categoryDTO) {
        String userId = categoryDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        String categoryId = categoryDTO.getCategoryId();
        int result = 0;
        if (StringUtils.isNotBlank(categoryId)){
            Category selectOne = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getCategoryName, categoryDTO.getCategoryName()));
            if(selectOne != null){
                if (!selectOne.getId().toString().equals(categoryId)){
                    throw new BizException(ResponseCodeEnum.CATEGORY_CATEGORYNAME_IS_EXIST);
                }
            }
            Category category = categoryMapper.selectById(categoryId);
            if(category != null){
                LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
                if (StringUtils.isNotEmpty(categoryDTO.getCategoryName())) {
                    updateWrapper.set(Category::getCategoryName, categoryDTO.getCategoryName());
                }
                if (StringUtils.isNotEmpty(categoryDTO.getCategoryUrl())) {
                    updateWrapper.set(Category::getCategoryUrl, categoryDTO.getCategoryUrl());
                }
                if (StringUtils.isNotEmpty(categoryDTO.getDescription())) {
                    updateWrapper.set(Category::getDescription, categoryDTO.getDescription());
                }
                updateWrapper.set(Category::getUpdateByUserId,userId);
                updateWrapper.eq(Category::getId, categoryId);
                result = categoryMapper.update(updateWrapper);
            }
        }else{
            throw new BizException(ResponseCodeEnum.TAG_TAGID_CAN_NOT_BE_NULL);
        }
        DMLVo dmlVo = new DMLVo();
        if(result >0){
            dmlVo.setId(categoryId);
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_ERROR);
        }
        return dmlVo;
    }

    private CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }
}




