package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.CategoryDTO;
import com.titancore.domain.entity.Category;
import com.titancore.domain.param.CategoryParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.CategoryVo;
import com.titancore.domain.vo.DMLVo;

public interface CategoryService extends IService<Category> {
    /**
     * 根据分类id查询分类
     * @param id
     * @return
     */
    CategoryVo queryCategoryById(Long id);

    /**
     * 创建分类
     * @param categoryDTO
     * @return
     */
    DMLVo createCategory(CategoryDTO categoryDTO);

    /**
     * 查询分类列表
     * @param categoryParam
     * @return
     */
    PageResult queryList(CategoryParam categoryParam);

    /**
     * 根据分类Id删除分类
     * @param categoryId
     * @return
     */
    DMLVo deleteCategoryByCategoryId(String categoryId);

    /**
     * 根据分类Id更新分类
     * @param categoryDTO
     * @return
     */
    DMLVo updateCategoryById(CategoryDTO categoryDTO);
}
