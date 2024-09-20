package com.titancore.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.Category;
import com.titancore.domain.mapper.CategoryMapper;
import com.titancore.domain.vo.CategoryVo;
import com.titancore.service.CategoryService;
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
        CategoryVo categoryVo = new CategoryVo();
        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getId, id));
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}




