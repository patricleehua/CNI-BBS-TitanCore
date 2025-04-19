package com.titancore.domain.dto;


import com.titancore.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class AiMessageMediaDTO extends BaseEntity {

    /**
     * 媒体类型
     */
    private String type;

    /**
     * 媒体url
     */
    private String url;

}
