package com.titancore.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户最近评论VO（包含帖子信息）
 */
@Data
public class UserRecentCommentVo {

    /**
     * 评论ID
     */
    private String commentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论时间
     */
    private LocalDateTime commentTime;

    /**
     * 评论层级 1-父评论 2-子评论
     */
    private Integer level;

    /**
     * 帖子ID
     */
    private String postId;

    /**
     * 帖子标题
     */
    private String postTitle;

    /**
     * 帖子简介/摘要
     */
    private String postSummary;

    /**
     * 帖子作者信息
     */
    private UserVo postAuthor;
}
