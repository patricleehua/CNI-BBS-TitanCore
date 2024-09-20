package com.titancore.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;



@Data
public class MediaUrlVo {

    private Long id;

    private String mediaUrl;

    private String mediaType;

    private LocalDateTime createDate;

}
