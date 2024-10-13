package com.titancore.controller;


import com.titancore.domain.dto.CategoryDTO;
import com.titancore.domain.param.CategoryParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.DMLVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@Tag(name = "帖子板块模块")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @PostMapping("/open/list")
    @Operation(summary = "获取板块列表" )
    public Response<?> queryList(@RequestBody CategoryParam categoryParam){
       PageResult pageResult =  categoryService.queryList(categoryParam);
       return Response.success(pageResult);
    }

    @PostMapping("/createCategory")
    @Operation(summary = "创建板块" )
    public Response<?> createTag(@RequestBody CategoryDTO categoryDTO){
        DMLVo DMLVo = categoryService.createCategory(categoryDTO);
        return Response.success(DMLVo);
    }
    @DeleteMapping("/deleteCategory")
    @Operation(summary = "删除板块" )
    public Response<?> deleteCategory(@RequestParam String categoryId){
        DMLVo dmlVo = categoryService.deleteCategoryByCategoryId(categoryId);
        return Response.success(dmlVo);
    }
    @PutMapping("/updateCategory")
    @Operation(summary = "更新板块")
    public Response<?> updateTag(@RequestBody CategoryDTO categoryDTO){
        DMLVo dmlVo = categoryService.updateCategoryById(categoryDTO);
        return Response.success(dmlVo);
    }
}
