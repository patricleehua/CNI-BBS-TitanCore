package com.titancore.domain.vo;

import lombok.Data;



@Data
public class UserRegisterVo{

    private String userId;
    private String publicUsername;
    private String nickName;
    private String email;
    private String avatar;
    private String status;
}
