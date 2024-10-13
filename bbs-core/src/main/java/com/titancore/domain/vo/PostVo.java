package com.titancore.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Data
public class PostVo {

    private String postId;

    private UserVo userVo;

    private String title;
    private String summary;
    private HashMap<String,String> postContent;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime commentTime;

    private CategoryVo categoryVo;
    private List<TagVo> tagVos;
    private List<MediaUrlVo> urls;

    private String type;
    private String view_counts;
}
