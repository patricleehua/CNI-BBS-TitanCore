package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.PointsRecordDTO;
import com.titancore.domain.entity.PointsRecord;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PointsRuleVo;

import java.time.LocalDateTime;

/**
* @author leehua
* @description 针对表【points_record(积分记录表)】的数据库操作Service
* @createDate 2024-10-31 15:45:30
*/
public interface PointsRecordService extends IService<PointsRecord> {

    /**
     * 添加积分记录
     * @param pointsRecordDTO
     * @return
     */
    DMLVo addPointsRecord(PointsRecordDTO pointsRecordDTO);

    static PointsRecordDTO buildPointsRecordDTO(PointsRuleVo pointsRuleVo, String userId ,String status){
        PointsRecordDTO pointsRecordDTO = new PointsRecordDTO();
        pointsRecordDTO.setUserId(userId);
        pointsRecordDTO.setRuleId(pointsRuleVo.getId());
        pointsRecordDTO.setPoints(pointsRuleVo.getPoints());
        pointsRecordDTO.setStatus(status);
        pointsRecordDTO.setExpirationTime(LocalDateTime.now().plusDays(pointsRuleVo.getValidityPeriod()));
        return pointsRecordDTO;
    }
}
