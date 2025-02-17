package com.titancore.domain.vo;


import lombok.Data;

import java.time.LocalDateTime;



@Data
public class CategoryVo {

    private String id;

    private String categoryName;

    private String categoryUrl;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
