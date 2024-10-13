package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import com.titancore.enums.LinkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@TableName(value ="media_url")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUrl {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 链接地址
     */
    private String mediaUrl;

    /**
     * 链接类型 封面、背景、视频、未知
     */
    @TableField("`media_type`")
    private LinkType mediaType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
