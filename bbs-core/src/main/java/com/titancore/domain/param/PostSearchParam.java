package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "PostSearchParam", description = "帖子查询传递参数对象")
public class PostSearchParam extends PageParams {
    @Schema(description = "搜索关键字")
    private String keyword;
    @Schema(description = "作者名")
    private String authorName;
    @Schema(description = "分类列表")
    private Long categoryId;
    @Schema(description = "标签列表")
    private List<Long> tagList;
    @Schema(description = "在xxx时间之前")
    private LocalDateTime beforeTime;
    @Schema(description = "在xxx时间之后")
    private LocalDateTime afterTime;
    @Schema(description = "是否升序")
    private Boolean isAsc = false;
    @Schema(description = "需要排序的字段")
    private String sortBy;
}
