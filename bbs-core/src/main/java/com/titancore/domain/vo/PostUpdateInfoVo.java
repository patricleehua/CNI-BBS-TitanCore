package com.titancore.domain.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class PostUpdateInfoVo {

    private String postId;
    private String userId;
    private String title;
    private String summary;
    private HashMap<String,String> postContent;

    private String categoryId;
    private List<String> tagIds;
    private List<PostMediaUrlVo> urls;

    private String type;
}
