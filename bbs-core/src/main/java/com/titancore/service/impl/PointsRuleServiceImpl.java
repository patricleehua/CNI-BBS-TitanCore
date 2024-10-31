package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.PointsRuleDTO;
import com.titancore.domain.entity.PointsRule;
import com.titancore.domain.mapper.PointsRuleMapper;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PointsRuleVo;
import com.titancore.enums.StatusEnum;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.service.PointsRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author leehua
* @description 针对表【points_rule(积分规则表)】的数据库操作Service实现
* @createDate 2024-10-31 15:45:30
*/
@Service
public class PointsRuleServiceImpl extends ServiceImpl<PointsRuleMapper, PointsRule>
    implements PointsRuleService {

    @Override
    public DMLVo addPointsRole(PointsRuleDTO pointsRuleDTO) {
        //todo 异常处理
        PointsRule pointsRule = new PointsRule();
        BeanUtils.copyProperties(pointsRuleDTO,pointsRule);
        PointsRule isExisting =  this.getOne(new LambdaQueryWrapper<PointsRule>().eq(PointsRule::getBehaviorType, pointsRule.getBehaviorType()).eq(PointsRule::getDescription, pointsRule.getDescription()));
        if(isExisting != null){
            //todo 抛出异常
            return null;
        }
        boolean save = this.save(pointsRule);
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

    @Override
    public PointsRuleVo queryRuleByUniqueKey(String uniqueKey) {
        PointsRule pointsRule = this.getOne(new LambdaQueryWrapper<PointsRule>().eq(PointsRule::getUniqueKey, uniqueKey).eq(PointsRule::getIsActive, StatusEnum.ENABLE.getValue()));
        if(pointsRule == null){
            //todo 处理异常
            return null;
        }
        PointsRuleVo pointsRuleVo = new PointsRuleVo();
        BeanUtils.copyProperties(pointsRule,pointsRuleVo);
        pointsRuleVo.setId(String.valueOf(pointsRule.getId()));
        return pointsRuleVo;
    }

    @Override
    public PointsRuleVo queryRuleById(String roleId) {
        PointsRule pointsRule = this.getOne(new LambdaQueryWrapper<PointsRule>().eq(PointsRule::getId, roleId).eq(PointsRule::getIsActive, StatusEnum.ENABLE.getValue()));
        if(pointsRule == null){
            //todo 处理异常
            return null;
        }
        PointsRuleVo pointsRuleVo = new PointsRuleVo();
        BeanUtils.copyProperties(pointsRule,pointsRuleVo);
        pointsRuleVo.setId(String.valueOf(pointsRule.getId()));
        return pointsRuleVo;
    }
}




