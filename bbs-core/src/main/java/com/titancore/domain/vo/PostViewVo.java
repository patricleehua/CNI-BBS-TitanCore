package com.titancore.domain.vo;


import com.titancore.domain.entity.MediaUrl;
import lombok.Data;

import javax.print.attribute.standard.Media;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostViewVo {

    private String postId;

    private String authorName;

    private String title;
    private String summary;

    private LocalDateTime create_date;
    private LocalDateTime update_date;
    private LocalDateTime comment_date;

    private CategoryVo categoryVo;
    private List<TagVo> tagVos;
    private List<MediaUrlVo> urls;

    private String type;
    private String view_counts;
    private String istop;

}
