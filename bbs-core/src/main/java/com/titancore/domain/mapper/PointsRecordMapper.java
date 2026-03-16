package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {

    /**
     * 查询用户总积分（ earned - used ）
     * @param userId
     * @return
     */
    @Select("SELECT COALESCE(SUM(points - COALESCE(used_points, 0)), 0) FROM points_record WHERE user_id = #{userId} AND status = 'EARNED'")
    Long selectTotalPointsByUserId(@Param("userId") Long userId);
}




