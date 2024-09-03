package com.titancore.domain.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserVo implements Serializable {
    private String userId;
    private String userName;
    private String avatar;

}
