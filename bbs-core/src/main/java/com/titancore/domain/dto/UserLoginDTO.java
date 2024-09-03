package com.titancore.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserLoginDTO implements Serializable {
    private String username;
    private String password;
}
