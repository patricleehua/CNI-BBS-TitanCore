package com.titancore.domain.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVo implements Serializable {

    //序列化机制 Long类型转String类型防止精度丢失的
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String token;
    private String avatar;
    private List<String> role;

}
