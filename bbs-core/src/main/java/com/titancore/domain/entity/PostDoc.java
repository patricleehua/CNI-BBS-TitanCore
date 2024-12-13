package com.titancore.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostDoc extends BaseEntity{

    //帖子id
    private String id;
    //标题
    private String title;
    //简介
    private String summary;
    //文章内容
    private String content;
    //作者名称
    private String authorName;

    //类别
    private Long categoryId;
    //标签
    private List<Long> tagsId;
    //类型 0/1图文视频
    private Integer type;
    //是否置顶
    private Integer isTop;

    //浏览数量
    private Integer viewCounts;
    //评论数量
    private Integer commentCounts;

    //封面url
    private String coverUrl;

    //创建时间
    private Long createdTime;
    //更新时间
    private Long updateTime;
    //最后评论时间
    private Long lastCommentTime;

}
