package com.titancore.controller;

import com.titancore.domain.dto.PostDTO;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PostVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.PostsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/post")
@Tag(name = "帖子模块")
public class PostController {
    @Autowired
    private PostsService postsService;
    @PostMapping("/open/queryPostList")
    @Operation(summary = "查询帖子列表" , description = "支持分页查询")
    public Response<?> queryPostList(@RequestBody PostParam postParam ){
        log.info("postParam:{}",postParam);
        PageResult postListVo = postsService.queryPostList(postParam);
        return Response.success(postListVo);
    }

    @GetMapping("/open/{postId}")
    @Operation(summary = "根据帖子Id查询帖子信息")
    public Response<?> queryPostDetaisById(@PathVariable("postId") String postId){
        PostVo postVo = postsService.queryPostDetaisById(postId);
        return Response.success(postVo);
    }
    @PostMapping("/createPost")
    @Operation(summary ="创建帖子")
    public Response<?> createPost(@RequestBody PostDTO postDTO){
        DMLVo dmlVo = postsService.createPost(postDTO);
        return Response.success(dmlVo);
    }

}
