package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "RetractionDTO", description = "重新编辑传输对象")
@NoArgsConstructor
public class RetractionDTO extends BaseDTO{
    @Schema(description = "当前用户Id")
    @NonNull
    private String userId;
    @Schema(description = "需要撤回消息Id")
    @NonNull
    private String msgId;


}
