package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@TableName(value ="posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {
    /**
     * post标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作者id
     */
    private Long authorId;

    /**
     * 标题
     */
    private String title;

    /**
     * 简介
     */
    private String summary;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 评论时间
     */
    private LocalDateTime commentDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 浏览量
     */
    private Integer viewCounts;

    /**
     * 内容id
     */
    private Long contentId;

    /**
     * 类别id
     */
    private Long categoryId;

    /**
     * 帖子格式图文0/视频1
     */
    private Integer type;

    /**
     * 是否置顶，1为不置顶，0为置顶
     */
    private Integer istop;

    /**
     * 发布状态，0为发布，1为未发布
     */
    private Integer ispublish;

    /**
     * 删除标志，0代表存在，1代表删除
     */
    private Integer delFlag;
}
