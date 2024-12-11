package com.titancore.controller;

import com.titancore.domain.dto.PostDTO;
import com.titancore.domain.dto.PostUpdateDTO;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PostFrequencyVo;
import com.titancore.domain.vo.PostUpdateInfoVo;
import com.titancore.domain.vo.PostVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.PostsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    @Operation(summary ="创建帖子(第二步)")
    public Response<?> createPost(@RequestBody PostDTO postDTO){
        DMLVo dmlVo = postsService.createPost(postDTO);
        return Response.success(dmlVo);
    }
    @GetMapping("/createTemporaryPostId")
    @Operation(summary ="创建获取临时帖子ID用作新增/更新(必须第一步)")
    public Response<?> createTemporaryPostId(@RequestParam String userId, @RequestParam(required = false) String postId){
        String temporaryPostId = postsService.createTemporaryPostId(userId,postId);
        return Response.success(temporaryPostId);
    }
    @GetMapping("/getUpdatePostInfo")
    @Operation(summary ="获取需要更新帖子的原始信息(第二步)")
    public Response<?> getUpdatePostInfo(@RequestParam String postId,@RequestParam String userId){
        PostUpdateInfoVo postUpdateInfoVo = postsService.getUpdatePostInfo(postId,userId);
        return Response.success(postUpdateInfoVo);
    }
    @PostMapping("/updatePost")
    @Operation(summary ="更新帖子(第三步)")
    public Response<?> updatePost(@RequestBody PostUpdateDTO postUpdateDTO){
        DMLVo dmlVo = postsService.updatePost(postUpdateDTO);
        return Response.success(dmlVo);
    }
    @DeleteMapping("/deletePost/{postId}")
    @Operation(summary ="删除帖子")
    public Response<?> deletePost(@PathVariable("postId") String postId){
        DMLVo dmlVo = postsService.deletePost(postId);
        return Response.success(dmlVo);
    }

    /**
     * 获取用户的发帖频率
     * @param userId 用户ID
     * @return 用户发帖频率数据
     */
    @GetMapping("/frequency")
    @Operation(summary ="用户发帖频率数据")
    public Response<?> getPostFrequency(@RequestParam String userId) {
        List<PostFrequencyVo> frequencyData = postsService.getPostFrequency(userId);
        return Response.success(frequencyData);
    }
}
