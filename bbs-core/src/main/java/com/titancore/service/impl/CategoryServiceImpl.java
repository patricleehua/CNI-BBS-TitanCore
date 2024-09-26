package com.titancore.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.CategoryDTO;
import com.titancore.domain.entity.Category;
import com.titancore.domain.mapper.CategoryMapper;
import com.titancore.domain.param.CategoryParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.CategoryVo;
import com.titancore.domain.vo.DDLVo;
import com.titancore.service.CategoryService;
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
        CategoryVo categoryVo = new CategoryVo();
        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getId, id));
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }

    @Override
    public DDLVo createCategory(CategoryDTO categoryDTO) {
        //todo 创建者须与当前登入用户一致

        //todo 异常处理 优化代码

        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        int result = categoryMapper.insert(category);
        DDLVo ddlVo = new DDLVo();
        if(result >0){
            ddlVo.setId(String.valueOf(category.getId()));
            ddlVo.setStatus(true);
            ddlVo.setMessage("插入成功");
        }else{
            ddlVo.setStatus(false);
            ddlVo.setMessage("插入失败");
        }
        return ddlVo;
    }

    @Override
    public PageResult queryList(CategoryParam categoryParam) {
        //todo 异常处理 redis
        Page<Category> page = new Page<>(categoryParam.getPageNo(),categoryParam.getPageSize());
        LambdaQueryWrapper<Category> queryWrapper =  new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(categoryParam.getCategoryName())){
            queryWrapper.eq(Category::getCategoryName,categoryParam.getCategoryName());
        }
        Page<Category> categoryPage = categoryMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(pageResult.getTotal());
        pageResult.setRecords(categoryPage.getRecords().stream().map(this::copy).toList());
        return pageResult;
    }

    private CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}




