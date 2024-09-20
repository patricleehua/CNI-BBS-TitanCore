package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import lombok.Data;

import java.util.List;

@Data
public class PostParam extends PageParams {
    private String postId;
    private String categoryId;
    private List<String> tagIds;
}
