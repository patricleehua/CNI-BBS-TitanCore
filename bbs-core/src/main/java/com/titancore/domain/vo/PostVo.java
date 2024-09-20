package com.titancore.domain.vo;

import com.titancore.domain.entity.PostContent;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Data
public class PostVo {

    private String postId;

    private String authorName;

    private String title;
    private String summary;
    private HashMap<String,String> postContent;

    private LocalDateTime create_date;
    private LocalDateTime update_date;
    private LocalDateTime comment_date;

    private CategoryVo categoryVo;
    private List<TagVo> tagVos;
    private List<MediaUrlVo> urls;

    private String type;
    private String view_counts;
}
