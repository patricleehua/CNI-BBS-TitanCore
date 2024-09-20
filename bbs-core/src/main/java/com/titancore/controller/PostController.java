package com.titancore.controller;

import com.titancore.domain.entity.Posts;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.PostVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.PostsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostsService postsService;
    @PostMapping("/queryPostList")
    public Response<?> queryPostList(@RequestBody PostParam postParam ){
        log.info("postParam:{}",postParam);
        PageResult postListVo = postsService.queryPostList(postParam);
        return Response.success(postListVo);
    }

    @GetMapping("/{postId}")
    public Response<?> queryPostDetaisById(@PathVariable("postId") String postId){
        PostVo postVo = postsService.queryPostDetaisById(postId);
        return Response.success(postVo);
    }

}
