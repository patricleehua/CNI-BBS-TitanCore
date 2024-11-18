package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.PostCommentsDTO;
import com.titancore.domain.entity.PostComments;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostCommentParam;
import com.titancore.domain.vo.DMLVo;

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
}
