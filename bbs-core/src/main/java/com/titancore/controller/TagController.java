package com.titancore.controller;


import com.titancore.domain.dto.TagDTO;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.TagParam;
import com.titancore.domain.vo.DMLVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tag")
@Tag(name = "帖子标签模块")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping("/open/list")
    @Operation(summary = "获取标签列表")
    public Response<?> queryList(@RequestBody TagParam tagParam) {
        PageResult pageResult = tagService.queryList(tagParam);
        return Response.success(pageResult);
    }

    @PostMapping("/createTag")
    @Operation(summary = "创建标签")
    public Response<?> createTag(@RequestBody TagDTO tagDTO) {
        DMLVo dmlVo = tagService.createTag(tagDTO);
        return Response.success(dmlVo);
    }

    @DeleteMapping("/deleteTag")
    @Operation(summary = "删除标签")
    public Response<?> deleteTag(@RequestParam String tagId){
        DMLVo dmlVo = tagService.deleteTagByTagId(tagId);
        return Response.success(dmlVo);
    }
    @PutMapping("/updateTag")
    @Operation(summary = "更新标签")
    public Response<?> updateTag(@RequestBody TagDTO tagDTO){
        DMLVo dmlVo = tagService.updateTagById(tagDTO);
        return Response.success(dmlVo);
    }
}
