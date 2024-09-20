package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
    @TableId(type = IdType.AUTO)
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
     * 链接类型
     */
    private int mediaType;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

}
