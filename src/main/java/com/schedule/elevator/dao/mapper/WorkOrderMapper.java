package com.schedule.elevator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.elevator.dto.*;
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
            "ROUND(AVG(CASE WHEN rescue_duration IS NOT NULL THEN rescue_duration/60.0 END), 2) as avgRescueDuration, " +
            "ROUND(AVG(CASE WHEN repair_duration IS NOT NULL THEN repair_duration/60.0 END), 2) as avgRepairDuration " +
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
            "  SUM(CASE WHEN order_type IN (1, 2, 5, 6) THEN 1 ELSE 0 END) AS count, ",  // 只统计困人和非困人故障
            "  SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) AS trapped_count, ",  // 困人故障
            "  SUM(CASE WHEN order_type = 2 THEN 1 ELSE 0 END) AS non_trapped_count, ",  // 非困人故障
            "  SUM(CASE WHEN order_type IN (5, 6) THEN 1 ELSE 0 END) AS other_count ",  // 其他（自行脱困、误报）
            "FROM work_order ",
            "WHERE 1 = 1 ",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "GROUP BY FLOOR(HOUR(create_time) / 2) ",
            "HAVING SUM(CASE WHEN order_type IN (1, 2) THEN 1 ELSE 0 END) > 0 ",  // 确保只包含有故障的时段
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

    /**
     * 查询符合条件的各 project_type 的数量（只返回存在的类型）
     */
    @Select({
            "<script>",
            "SELECT project_type AS projectCode, COUNT(*) AS count ",
            "FROM work_order ",
            "WHERE project_type IS NOT NULL ",
            "  AND project_type != '' ",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.status != null'>",
            "  AND status = #{searchDTO.status}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            // 可继续添加其他条件...
            "GROUP BY project_type",
            "</script>"
    })
    List<ProjectTypeCountDTO> getProjectTypeCounts(@Param("searchDTO") SearchDTO searchDTO);


    @Select({
            "<script>",
            "SELECT ",
            "<![CDATA[",
            "  CASE ",
            "    WHEN time_to_arrive >= 0 AND time_to_arrive < 5 THEN '0-5分钟' ",
            "    WHEN time_to_arrive >= 5 AND time_to_arrive < 10 THEN '5-10分钟' ",
            "    WHEN time_to_arrive >= 10 AND time_to_arrive < 15 THEN '10-15分钟' ",
            "    WHEN time_to_arrive >= 15 AND time_to_arrive < 20 THEN '15-20分钟' ",
            "    WHEN time_to_arrive >= 20 AND time_to_arrive < 25 THEN '20-25分钟' ",
            "    WHEN time_to_arrive >= 25 AND time_to_arrive < 30 THEN '25-30分钟' ",
            "    WHEN time_to_arrive >= 30 THEN '30分钟以上' ",
            "    ELSE '其他' ",
            "  END AS time_range, ",
            "  SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) AS trappedArrivalCount, ",
            "  SUM(CASE WHEN order_type = 2 THEN 1 ELSE 0 END) AS nonTrappedArrivalCount, ",
            "  CASE ",
            "    WHEN time_to_arrive >= 0 AND time_to_arrive < 5 THEN 1 ",
            "    WHEN time_to_arrive >= 5 AND time_to_arrive < 10 THEN 2 ",
            "    WHEN time_to_arrive >= 10 AND time_to_arrive < 15 THEN 3 ",
            "    WHEN time_to_arrive >= 15 AND time_to_arrive < 20 THEN 4 ",
            "    WHEN time_to_arrive >= 20 AND time_to_arrive < 25 THEN 5 ",
            "    WHEN time_to_arrive >= 25 AND time_to_arrive < 30 THEN 6 ",
            "    WHEN time_to_arrive >= 30 THEN 7 ",
            "    ELSE 8 ",
            "  END AS sort_no ",
            "]]>",
            "FROM work_order ",
            "WHERE ",
            "<![CDATA[",
            "  time_to_arrive IS NOT NULL ",
            "  AND order_type IN (1, 2) ",
            "]]>",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and !searchDTO.district.isEmpty()'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "<![CDATA[",
            "GROUP BY time_range, sort_no ",
            "ORDER BY sort_no ASC",
            "]]>",
            "</script>"
    })
    List<TimeConsumptionStatsDTO> getArriveTimeConsumptionStats(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "  '总计' AS time_range, ",
            "  COUNT(CASE WHEN order_type = 1 THEN 1 END) AS trappedArrivalCount, ",
            "  COUNT(CASE WHEN order_type = 2 THEN 1 END) AS nonTrappedArrivalCount ",
            "FROM work_order ",
            "WHERE time_to_arrive IS NOT NULL ",
            "  AND order_type IN (1, 2) ",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "</script>"
    })
    TimeConsumptionStatsDTO getTotalArriveTimeConsumptionStats(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "<![CDATA[",
            "  CASE ",
            "    WHEN rescue_duration >= 0 AND rescue_duration < 5 THEN '0-5分钟' ",
            "    WHEN rescue_duration >= 5 AND rescue_duration < 10 THEN '5-10分钟' ",
            "    WHEN rescue_duration >= 10 AND rescue_duration < 15 THEN '10-15分钟' ",
            "    WHEN rescue_duration >= 15 AND rescue_duration < 20 THEN '15-20分钟' ",
            "    WHEN rescue_duration >= 20 AND rescue_duration < 25 THEN '20-25分钟' ",
            "    WHEN rescue_duration >= 25 AND rescue_duration < 30 THEN '25-30分钟' ",
            "    WHEN rescue_duration >= 30 THEN '30分钟以上' ",
            "    ELSE '其他' ",
            "  END AS time_range, ",
            "  SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) AS trappedRescueCount, ",
            "  CASE ",
            "    WHEN rescue_duration >= 0 AND rescue_duration < 5 THEN 1 ",
            "    WHEN rescue_duration >= 5 AND rescue_duration < 10 THEN 2 ",
            "    WHEN rescue_duration >= 10 AND rescue_duration < 15 THEN 3 ",
            "    WHEN rescue_duration >= 15 AND rescue_duration < 20 THEN 4 ",
            "    WHEN rescue_duration >= 20 AND rescue_duration < 25 THEN 5 ",
            "    WHEN rescue_duration >= 25 AND rescue_duration < 30 THEN 6 ",
            "    WHEN rescue_duration >= 30 THEN 7 ",
            "    ELSE 8 ",
            "  END AS sort_no ",
            "]]>",
            "FROM work_order ",
            "WHERE ",
            "<![CDATA[",
            "  rescue_duration IS NOT NULL ",
            "  AND order_type IN (1, 2) ",
            "]]>",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and !searchDTO.district.isEmpty()'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "<![CDATA[",
            "GROUP BY time_range, sort_no ",
            "ORDER BY sort_no ASC",
            "]]>",
            "</script>"
    })
    List<TimeConsumptionStatsDTO> getRescueTimeConsumptionStats(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT COUNT(*) AS trappedRescueCount ",
            "FROM work_order ",
            "WHERE rescue_duration IS NOT NULL ",
            "  AND order_type = 1 ",  // 困人工单
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "</script>"
    })
    Integer getTrappedRescueCount(@Param("searchDTO") SearchDTO searchDTO);

}