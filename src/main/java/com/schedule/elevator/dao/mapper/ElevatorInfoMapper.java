package com.schedule.elevator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ElevatorInfoMapper extends BaseMapper<ElevatorInfo> {
    @Select("<script>" +
            "SELECT " +
            "    elevator_type AS elevatorType, " +
            "    COUNT(*) AS elevatorCount " +
            "FROM elevator_info " +
            "WHERE elevator_type IS NOT NULL " +
            "  AND elevator_type != '' " +
            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'> " +
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if> " +
            "GROUP BY elevator_type" +
            "</script>")
    List<Map<String, Object>> countByElevatorType(@Param("searchDTO") SearchDTO searchDTO);


}
