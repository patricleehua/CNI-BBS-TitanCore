package com.titancore.controller;

import com.titancore.domain.dto.CleanCoverDTO;
import com.titancore.domain.dto.PostCommentsDTO;
import com.titancore.domain.dto.PostDTO;
import com.titancore.domain.dto.PostUpdateDTO;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostCommentParam;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PostCommentVo;
import com.titancore.domain.vo.PostFrequencyVo;
import com.titancore.domain.vo.UserRecentCommentVo;
import com.titancore.domain.vo.PostUpdateInfoVo;
import com.titancore.domain.vo.PostVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.PostCommentsService;
import com.titancore.service.PostsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequestMapping("/post")
@Tag(name = "帖子模块")
public class PostController {
    @Autowired
    private PostsService postsService;
    @Autowired
    private PostCommentsService postCommentsService;
    @PostMapping("/open/queryPostList")
    @Operation(summary = "查询帖子列表" , description = "支持分页查询")
    public Response<PageResult> queryPostList(@RequestBody PostParam postParam ){
        log.info("postParam:{}",postParam);
        PageResult postListVo = postsService.queryPostList(postParam);
        return Response.success(postListVo);
    }

    @GetMapping("/open/{postId}")
    @Operation(summary = "根据帖子Id查询帖子信息")
    public Response<PostVo> queryPostDetaisById(@PathVariable("postId") String postId){
        PostVo postVo = postsService.queryPostDetaisById(postId);
        return Response.success(postVo);
    }
    @PostMapping("/createPost")
    @Operation(summary ="创建帖子(第二步)")
    public Response<DMLVo> createPost(@RequestBody PostDTO postDTO){
        DMLVo dmlVo = postsService.createPost(postDTO);
        return Response.success(dmlVo);
    }
    @PostMapping("/cleanTemporaryCover")
    @Operation(summary ="清除创建帖子中反复上传的封面")
    public Response<DMLVo> cleanTemporaryCover(@RequestBody CleanCoverDTO cleanCoverDTO) {
        DMLVo dmlVo = postsService.cleanTemporaryCover(cleanCoverDTO);
        return Response.success(dmlVo);
    }
    @GetMapping("/createTemporaryPostId")
    @Operation(summary ="创建获取临时帖子ID用作新增/更新(必须第一步)")
    public Response<String> createTemporaryPostId(@RequestParam String userId, @RequestParam(required = false) String postId){
        String temporaryPostId = postsService.createTemporaryPostId(userId,postId);
        return Response.success(temporaryPostId);
    }
    @GetMapping("/getUpdatePostInfo")
    @Operation(summary ="获取需要更新帖子的原始信息(第二步)")
    public Response<PostUpdateInfoVo> getUpdatePostInfo(@RequestParam String postId,@RequestParam String userId){
        PostUpdateInfoVo postUpdateInfoVo = postsService.getUpdatePostInfo(postId,userId);
        return Response.success(postUpdateInfoVo);
    }
    @PostMapping("/updatePost")
    @Operation(summary ="更新帖子(第三步)")
    public Response<DMLVo> updatePost(@RequestBody PostUpdateDTO postUpdateDTO){
        DMLVo dmlVo = postsService.updatePost(postUpdateDTO);
        return Response.success(dmlVo);
    }
    @DeleteMapping("/deletePost/{postId}")
    @Operation(summary ="删除帖子")
    public Response<DMLVo> deletePost(@PathVariable("postId") String postId){
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
    public Response<List<PostFrequencyVo>> getPostFrequency(@RequestParam String userId) {
        List<PostFrequencyVo> frequencyData = postsService.getPostFrequency(userId);
        return Response.success(frequencyData);
    }

    /**
     *  获取帖子评论分页
     * @param postCommentParam
     * @return
     */
    @PostMapping("/open/queryCommentListByPostId")
    @Operation(summary = "获取帖子评论分页")
    private Response<PageResult> queryCommentListByPostId(@RequestBody PostCommentParam postCommentParam){
        PageResult pageResult = postCommentsService.queryCommentListByPostId(postCommentParam);
        return Response.success(pageResult);
    }
    @PostMapping("/addPostComment")
    @Operation(summary = "新增帖子评论")
    private Response<Map<String,String>> addPostComment(@RequestBody PostCommentsDTO postCommentsDTO){
        Map<String,String> map=postCommentsService.addPostComment(postCommentsDTO);
        return Response.success(map);
    }
    @DeleteMapping("/deletePostComment")
    @Operation(summary = "删除帖子评论")
    private Response<DMLVo> deletePostComment(@RequestBody PostCommentParam postCommentParam){
        DMLVo dmlVo = postCommentsService.deletePostComment(postCommentParam);
        return Response.success(dmlVo);
    }

    /**
     * 获取用户最近的评论列表
     * @param userName 用户公开用户名
     * @param limit 限制条数（默认10条）
     * @return 评论列表
     */
    @GetMapping("/open/recentComments/{userName}")
    @Operation(summary = "获取用户最近评论", description = "根据用户名获取用户最近的评论列表，包含帖子信息")
    public Response<List<UserRecentCommentVo>> getRecentCommentsByUserName(
            @PathVariable("userName") String userName,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<UserRecentCommentVo> comments = postCommentsService.queryRecentCommentsByUserName(userName, limit);
        return Response.success(comments);
    }
}
