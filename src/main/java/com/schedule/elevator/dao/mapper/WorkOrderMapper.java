package com.schedule.elevator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.elevator.dto.RescueLevelStatsDTO;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.dto.TimeSlotStatsDTO;
import com.schedule.elevator.dto.WorkOrderStatisticsDTO;
import com.schedule.elevator.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) as totalEvents, " +
            "SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) as trappedEvents, " +
            "SUM(CASE WHEN order_type = 2 THEN 1 ELSE 0 END) as nonTrappedEvents, " +
            "SUM(CASE WHEN order_type IN (3, 4) THEN 1 ELSE 0 END) as otherEvents, " +
            "SUM(trapped_count) as rescuedPeople, " +
            "ROUND(AVG(CASE WHEN order_type = 1 AND time_to_arrive IS NOT NULL THEN time_to_arrive/60.0 END), 2) as avgArrivalTimeForTrapped, " +
            "ROUND(AVG(CASE WHEN order_type = 2 AND time_to_arrive IS NOT NULL THEN time_to_arrive/60.0 END), 2) as avgArrivalTimeForNonTrapped, " +
            "ROUND(AVG(CASE WHEN rescue_duration IS NOT NULL THEN rescue_duration/60.0 END), 2) as avgRescueDuration " +
            "FROM work_order " +
            "<where>" +
            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>" +
            "AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if>" +
            "<if test='searchDTO.district != null and searchDTO.district != \"\"'>" +
            "AND district = #{searchDTO.district} " +
            "</if>" +
            "<if test='searchDTO.status != null'>" +
            "AND status = #{searchDTO.status} " +
            "</if>" +
            "</where>" +
            "</script>")
    WorkOrderStatisticsDTO getWorkOrderStatisticsByCondition(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 获取24小时内每2个小时的故障统计：1-2，2-3，3-4，4-5，5-6，6-7，7-8，8-9，9-10，10-11，11-12，12-13，13-14，14-15，15-16，16-17，17-18，18-19，19-20，20-21，21-22，22-23，23-24
     *
     * @param searchDTO
     * @return
     */
    @Select({
            "<script>",
            "SELECT ",
            "  CONCAT(FLOOR(HOUR(create_time) / 2) * 2, '-', FLOOR(HOUR(create_time) / 2) * 2 + 2) AS time_slot, ",
            "  COUNT(*) AS count ",
            "FROM work_order ",
            "WHERE 1 = 1 ",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "GROUP BY FLOOR(HOUR(create_time) / 2) ",
            "ORDER BY FLOOR(HOUR(create_time) / 2)",
            "</script>"
    })
    List<TimeSlotStatsDTO> getFaultStatsByTimeSlot(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 按救援级别统计（1/2/3级），支持动态条件
     */
    @Select({
            "<script>",
            "SELECT ",
            "  SUM(CASE WHEN rescue_level = 1 THEN 1 ELSE 0 END) AS level1,",
            "  SUM(CASE WHEN rescue_level = 2 THEN 1 ELSE 0 END) AS level2,",
            "  SUM(CASE WHEN rescue_level = 3 THEN 1 ELSE 0 END) AS level3,",
            "  COUNT(*) AS total",
            "FROM work_order ",
            "WHERE 1 = 1 ",
            // 时间范围
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            // 其他可选条件（按需添加）
            "<if test='searchDTO != null and searchDTO.status != null'>",
            "  AND status = #{searchDTO.status}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.projectType != null and searchDTO.projectType != \"\"'>",
            "  AND project_type = #{searchDTO.projectType}",
            "</if>",
            // 只统计有效工单（排除逻辑删除等，若需要）
            // "AND `delete` = 0",
            "</script>"
    })
    RescueLevelStatsDTO getRescueLevelStats(@Param("searchDTO") SearchDTO searchDTO);
}