package com.titancore.domain.vo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostViewVo {

    private String postId;

    private UserVo userVo;

    private String title;
    private String summary;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime commentTime;

    private CategoryVo categoryVo;
    private List<TagVo> tagVos;
    private List<MediaUrlVo> urls;

    private String type;
    private String view_counts;
    private String isTop;


}
