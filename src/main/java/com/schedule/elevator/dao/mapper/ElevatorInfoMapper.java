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
            "  AND created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if> " +
            "GROUP BY elevator_type" +
            "</script>")
    List<Map<String, Object>> countByElevatorType(@Param("searchDTO") SearchDTO searchDTO);

    @Select("<script>" +
            "SELECT " +
            "    district AS district, " +
            "    COUNT(*) AS elevatorCount, " +
            "    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM elevator_info " +
            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'> " +
            "  WHERE created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if>), 2) AS percentage " +
            "FROM elevator_info " +
            "WHERE district IS NOT NULL " +
            "  AND district != '' " +
            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'> " +
            "  AND created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if> " +
            "GROUP BY district" +
            "</script>")
    List<Map<String, Object>> countByDistrict(@Param("searchDTO") SearchDTO searchDTO);

    @Select("<script>" +
            "SELECT " +
            "    COUNT(*) AS totalElevators, " +
            "    SUM(CASE WHEN created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} THEN 1 ELSE 0 END) AS newElevators " +
            "FROM elevator_info " +
            "WHERE created_at IS NOT NULL " +
            "  AND #{searchDTO.createTimeStart} IS NOT NULL " +
            "  AND #{searchDTO.createTimeEnd} IS NOT NULL" +
            "</script>")
    Map<String, Object> countNewElevators(@Param("searchDTO") SearchDTO searchDTO);

}
