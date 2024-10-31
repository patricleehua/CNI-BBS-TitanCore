package com.titancore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.titancore.domain.dto.PointsRecordDTO;
import com.titancore.domain.entity.PointsRecord;
import com.titancore.domain.mapper.PointsRecordMapper;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PointsRuleVo;
import com.titancore.enums.PointsStatus;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.StatusEnum;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.PointsRecordService;
import com.titancore.service.PointsRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
* @author leehua
* @description 针对表【points_record(积分记录表)】的数据库操作Service实现
* @createDate 2024-10-31 15:45:30
*/
@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord>
    implements PointsRecordService {

    @Autowired
    private PointsRuleService pointsRuleService;

    @Override
    public DMLVo addPointsRecord(PointsRecordDTO pointsRecordDTO) {
        //校验DTO数据是否正确  todo 是否只允许一次的数据
        PointsRuleVo pointsRuleVo = pointsRuleService.queryRuleById(pointsRecordDTO.getRuleId());
        if(pointsRuleVo == null){
            throw new BizException(ResponseCodeEnum.POINTS_RULE_IS_NULL);
        }else if(!Objects.equals(pointsRuleVo.getPoints(), pointsRecordDTO.getPoints())){
            throw new BizException(ResponseCodeEnum.POINTS_IS_NOT_MATCH);
        }else if(!Objects.equals(pointsRuleVo.getIsActive(), StatusEnum.ENABLE.getValue())){
            throw new BizException(ResponseCodeEnum.POINTS_RULE_IS_NOT_ACTIVE);
        }

        PointsRecord pointsRecord = new PointsRecord();
        BeanUtils.copyProperties(pointsRecordDTO,pointsRecord);
        PointsStatus pointsStatus = PointsStatus.valueOfAll(pointsRecordDTO.getStatus());
        if(pointsStatus != null){
            pointsRecord.setStatus(pointsStatus);
        }
        pointsRecord.setUserId(Long.valueOf(pointsRecordDTO.getUserId()));
        pointsRecord.setRuleId(Long.valueOf(pointsRecordDTO.getRuleId()));
        pointsRecord.setExpirationTime(LocalDateTime.now().plusDays(pointsRuleVo.getValidityPeriod()));
        boolean save = this.save(pointsRecord);
        DMLVo dmlVo = new DMLVo();
        if(save){
            dmlVo.setMessage(CommonConstant.DML_CREATE_SUCCESS);
            dmlVo.setStatus(true);
        }else{
            dmlVo.setMessage(CommonConstant.DML_CREATE_ERROR);
            dmlVo.setStatus(false);
        }
        return dmlVo;
    }
}




