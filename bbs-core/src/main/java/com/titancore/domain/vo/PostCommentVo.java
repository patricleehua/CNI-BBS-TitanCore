package com.titancore.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostCommentVo{

    private String id;

    private UserVo user;

    private String content;

    private List<PostCommentVo> childrens;

    private LocalDateTime commentTime;

    private Integer level;

    private UserVo toUser;
}
