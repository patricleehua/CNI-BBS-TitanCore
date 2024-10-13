package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@TableName(value ="tag")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    /**
     * tagId
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签 URL
     */
    private String tagUrl;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 归属的板块 ID
     */
    private Long categoryId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者 ID
     */
    private Long createdByUserId;

    /**
     * 更新者 ID
     */
    private Long updateByUserId;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
