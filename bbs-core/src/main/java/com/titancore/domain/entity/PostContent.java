package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName(value ="post_content")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostContent{
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联的Postid
     */
    private Long postId;

    /**
     * post内容
     */
    private String content;

    /**
     * 文章内容的 HTML 格式
     */
    private String contentHtml;

}
