package com.titancore.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;



@Data
public class MediaUrlVo {

    private String id;

    private String mediaUrl;

    private String mediaType;

    private LocalDateTime createTime;

}
