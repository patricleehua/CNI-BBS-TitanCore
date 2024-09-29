package com.titancore.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "操纵对象结果返回对象")
public class DDLVo {

    @Schema(description = "id")
    private String id;
    @Schema(description = "状态")
    private boolean status = false;
    @Schema(description = "结果")
    private String message;

}
