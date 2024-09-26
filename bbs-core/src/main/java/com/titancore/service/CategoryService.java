package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.CategoryDTO;
import com.titancore.domain.entity.Category;
import com.titancore.domain.param.CategoryParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.CategoryVo;
import com.titancore.domain.vo.DDLVo;

public interface CategoryService extends IService<Category> {

    CategoryVo queryCategoryById(Long id);

    DDLVo createCategory(CategoryDTO categoryDTO);

    PageResult queryList(CategoryParam categoryParam);
}
