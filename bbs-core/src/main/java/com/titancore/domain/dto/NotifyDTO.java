package com.titancore.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;


@Data
@NoArgsConstructor
@Schema(name = "NotifyDTO", description = "通知传输对象")
public class NotifyDTO{

    @Schema(description = "id")
    @NonNull
    private String id;

    @Schema(description = "类型")
    @NonNull
    private String type;

    @Schema(description = "内容")
    @NonNull
    private NotifyContent content;

    @Schema(description = "创建时间")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Data
    public static class NotifyContent {
       @Schema(description = "标题")
        private String title;

        @Schema(description = "图片路径")
        private String img;

        @Schema(description = "文本内容")
        private String text;

    }

}
