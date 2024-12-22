package com.titancore.controller;

import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostSearchParam;
import com.titancore.framework.common.response.Response;
import com.titancore.service.ElasticSearch8Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/es")
@Tag(name = "es搜索模块")
public class ElasticSearchController {
    @Resource
    private ElasticSearch8Service elasticSearchService;

    @PostMapping("/postSearch")
    @Operation(summary = "es全文检索查询")
    public Response<?> postSearch(@RequestBody PostSearchParam postSearchParam){
        PageResult pageResult = elasticSearchService.searchPostPage(postSearchParam);
        return Response.success(pageResult);
    }

}
