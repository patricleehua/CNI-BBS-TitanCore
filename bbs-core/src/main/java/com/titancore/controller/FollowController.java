package com.titancore.controller;

import com.titancore.domain.dto.FollowDTO;
import com.titancore.domain.param.FollowParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.DMLVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/follow")
@Tag(name = "实时通讯-账户关系(好友/关注者)模块")
public class FollowController {
    @Autowired
    private FollowService followService;

    @PostMapping("/list")
    @Operation(summary = "获取关注列表")
    public Response<?> queryList(@RequestBody FollowParam followParam){
        PageResult page =followService.queryList(followParam);
        return Response.success(page);
    }

    @PostMapping("/buildFollow")
    @Operation(summary = "建立关注")
    public Response<?> buildFollow(@RequestBody FollowDTO followDTO){
        DMLVo dmlVo = followService.buildFollow(followDTO);
        return Response.success(dmlVo);
    }

    @PostMapping("/changeFollowStatus")
    @Operation(summary = "改变关注状态")
    public Response<?> changeFollowStatus(@RequestBody FollowDTO followDTO){
        DMLVo dmlVo = followService.changeFollowStatus(followDTO);
        return Response.success(dmlVo);
    }

    @PostMapping("/changeBlockStatus")
    @Operation(summary = "改变单向拉黑状态")
    public Response<?> blockFollower(@RequestBody FollowDTO followDTO){
        DMLVo dmlVo = followService.changeBlockStatus(followDTO);
        return Response.success(dmlVo);
    }
    @PostMapping("/removeFollow")
    @Operation(summary = "取消关注彼此")
    public Response<?> removeFollow(@RequestBody FollowDTO followDTO){
        DMLVo dmlVo = followService.removeFollow(followDTO);
        return Response.success(dmlVo);
    }

}
