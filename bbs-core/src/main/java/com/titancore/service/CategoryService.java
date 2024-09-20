package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.entity.Category;
import com.titancore.domain.vo.CategoryVo;

public interface CategoryService extends IService<Category> {

    CategoryVo queryCategoryById(Long id);

}
