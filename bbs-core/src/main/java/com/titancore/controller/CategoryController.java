package com.titancore.controller;


import com.titancore.domain.dto.CategoryDTO;
import com.titancore.domain.param.CategoryParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.DDLVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@Tag(name = "分类模块")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @PostMapping("/open/list")
    @Operation(summary = "获取分类列表" )
    public Response<?> queryList(@RequestBody CategoryParam categoryParam){
       PageResult pageResult =  categoryService.queryList(categoryParam);
       return Response.success(pageResult);
    }

    @PostMapping("/createCategory")
    @Operation(summary = "创建分类" )
    public Response<?> createTag(@RequestBody CategoryDTO categoryDTO){
        DDLVo ddlVo = categoryService.createCategory(categoryDTO);
        return Response.success(ddlVo);
    }
}
