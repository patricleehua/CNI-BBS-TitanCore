package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.PostCommentsDTO;
import com.titancore.domain.entity.PostComments;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostCommentParam;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PostCommentVo;
import com.titancore.domain.vo.UserRecentCommentVo;

import java.util.List;
import java.util.Map;

/**
* @author leehua
* @description 针对表【post_comments(帖子评论表)】的数据库操作Service
* @createDate 2024-12-12 13:07:11
*/
public interface PostCommentsService extends IService<PostComments> {

    PageResult queryCommentListByPostId(PostCommentParam postCommentParam);

    Map<String, String> addPostComment(PostCommentsDTO postCommentsDTO);

    DMLVo deletePostComment(PostCommentParam postCommentParam);

    /**
     * 获取用户最近的评论列表
     * @param publicUsername 用户公开用户名
     * @param limit 限制条数
     * @return 评论列表（包含帖子信息）
     */
    List<UserRecentCommentVo> queryRecentCommentsByUserName(String publicUsername, Integer limit);
}
