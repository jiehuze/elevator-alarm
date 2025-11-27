package com.schedule.elevator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.elevator.entity.FaultCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FaultCategoryMapper extends BaseMapper<FaultCategory> {
    // 不需要额外方法！所有查询由 Service 通过 MyBatis-Plus LambdaQuery 完成
}