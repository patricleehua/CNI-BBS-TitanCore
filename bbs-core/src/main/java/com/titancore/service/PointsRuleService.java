package com.titancore.service;

import com.titancore.domain.dto.PointsRuleDTO;
import com.titancore.domain.entity.PointsRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PointsRuleVo;

/**
* @author leehua
* @description 针对表【points_rule(积分规则表)】的数据库操作Service
* @createDate 2024-10-31 15:45:30
*/
public interface PointsRuleService extends IService<PointsRule> {

    /**
     * 添加规则
     * @param pointsRuleDTO
     * @return
     */
    DMLVo addPointsRole(PointsRuleDTO pointsRuleDTO);

    /**
     * 根据唯一键查询积分规则明细
     * @param uniqueKey
     * @return
     */
    PointsRuleVo queryRuleByUniqueKey(String uniqueKey);

    /**
     * 根据RULE_ID查询积分规则明细
     * @param roleId
     * @return
     */
    PointsRuleVo queryRuleById(String roleId);
}
