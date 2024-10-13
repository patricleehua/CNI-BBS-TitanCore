package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@TableName(value ="category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category{
    /**
     * categoryid
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 板块名称
     */
    private String categoryName;

    /**
     * 板块的 URL
     */
    private String categoryUrl;

    /**
     * 板块描述
     */
    private String description;

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
