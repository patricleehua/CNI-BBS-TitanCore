package com.titancore.domain.vo;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class TagVo {

    private String id;

    private String tagName;

    private String tagUrl;

    private String description;

    private String categoryId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
