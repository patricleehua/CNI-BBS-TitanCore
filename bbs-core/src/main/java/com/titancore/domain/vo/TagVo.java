package com.titancore.domain.vo;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class TagVo {

    private Long id;

    private String tagName;

    private String tagUrl;

    private String description;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

}
