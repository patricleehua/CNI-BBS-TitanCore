package com.titancore.domain.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserVo implements Serializable {

    private String userId;
    private String userName;
    private String avatar;
    private String bio;
    private String followingCount;
    private String fansCount;
    private String followStatus;
}
