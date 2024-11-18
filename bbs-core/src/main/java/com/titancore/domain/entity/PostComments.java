package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子评论表
 * @TableName post_comments
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="post_comments")
@Data
public class PostComments extends  BaseEntity{
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 父评论的评论id，如果是直接评论文章就设为0(子评论用)
     */
    private Long parentId;

    /**
     * 如果是父评论则为作者id，如果为子评论则id为被回复人的id
     */
    private Long toUid;

    /**
     * 当前评论人的id
     */
    private Long userId;

    /**
     * 回复的对方评论id，如果自己是父评论则设为0(子评论用)
     */
    private Long replyCommentId;

    /**
     * 评论层级，1为父评论，2为子评论
     */
    private Integer level;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论时间
     */
    private LocalDateTime commentTime;

}