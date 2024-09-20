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


@TableName(value ="category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category{
    /**
     * categoryid
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类的 URL
     */
    private String categoryUrl;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 创建者 ID
     */
    private Long createdByUserId;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

}
