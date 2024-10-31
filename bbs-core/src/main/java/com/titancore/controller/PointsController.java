package com.titancore.controller;


import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.titancore.domain.dto.PointsRecordDTO;
import com.titancore.domain.dto.PointsRuleDTO;
import com.titancore.domain.entity.PointsRecord;
import com.titancore.domain.vo.DMLVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.PointsRecordService;
import com.titancore.service.PointsRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "积分管理")
@RequestMapping("/points")
public class PointsController {
    @Autowired
    private PointsRuleService pointsRuleService;
    @Autowired
    private PointsRecordService pointsRecordService;

    @PostMapping("/addPointsRole")
    @SaCheckOr(role = {@SaCheckRole("superpower_user"),@SaCheckRole("admin_user")})
    @Operation(summary = "新增积分规则")
    public Response<?> addPointsRole(@RequestBody PointsRuleDTO pointsRuleDTO){
        DMLVo dmlVo = pointsRuleService.addPointsRole(pointsRuleDTO);
        return Response.success(dmlVo);
    }

    @PostMapping("/addPointsRecord")
    @Operation(summary = "新增用户积分记录")
    public Response<?> addPointsRecord(@RequestBody PointsRecordDTO pointsRecordDTO){
        DMLVo dmlVo = pointsRecordService.addPointsRecord(pointsRecordDTO);
        return Response.success(dmlVo);
    }
}
