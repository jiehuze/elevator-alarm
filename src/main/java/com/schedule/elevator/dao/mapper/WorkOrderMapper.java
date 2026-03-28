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
            "SUM(CASE WHEN order_type IN (5, 6) THEN 1 ELSE 0 END) as otherEvents, " +
            "SUM(trapped_count) as rescuedPeople, " +
            "ROUND(AVG(CASE WHEN order_type = 1 AND time_to_arrive IS NOT NULL THEN time_to_arrive/60.0 END), 2) as avgArrivalTimeForTrapped, " +
            "ROUND(AVG(CASE WHEN order_type = 2 AND time_to_arrive IS NOT NULL THEN time_to_arrive/60.0 END), 2) as avgArrivalTimeForNonTrapped, " +
            "ROUND(AVG(CASE WHEN rescue_duration IS NOT NULL THEN rescue_duration/60.0 END), 2) as avgRescueDuration, " +
            "ROUND(AVG(CASE WHEN repair_duration IS NOT NULL THEN repair_duration/60.0 END), 2) as avgRepairDuration " +
            "FROM work_order " +
            "<where>" +
            "AND order_type IN (1, 2, 5, 6) " +
            "AND status = 99 " +
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
            "AND status = 99 ",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
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
            "AND status = 99 ",
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
            "</script>"
    })
    RescueLevelStatsDTO getRescueLevelStats(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 查询符合条件的各 project_type 的数量（只返回存在的类型）
     */
    @Select({
            "<script>",
            "SELECT project_type AS projectCode, COUNT(*) AS faultCount ",
            "FROM work_order ",
            "WHERE project_type IS NOT NULL ",
            "  AND project_type != '' ",
            "AND status = 99 ",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.status != null'>",
            "  AND status = #{searchDTO.status}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "GROUP BY project_type",
            "</script>"
    })
    List<ProjectTypeCountDTO> getProjectTypeCounts(@Param("searchDTO") SearchDTO searchDTO);


    @Select({
            "<script>",
            "SELECT ",
            "<![CDATA[",
            "  CASE ",
            "    WHEN time_to_arrive >= 0 AND time_to_arrive < 300 THEN '0-5分钟' ",
            "    WHEN time_to_arrive >= 300 AND time_to_arrive < 600 THEN '5-10分钟' ",
            "    WHEN time_to_arrive >= 600 AND time_to_arrive < 900 THEN '10-15分钟' ",
            "    WHEN time_to_arrive >= 900 AND time_to_arrive < 1200 THEN '15-20分钟' ",
            "    WHEN time_to_arrive >= 1200 AND time_to_arrive < 1500 THEN '20-25分钟' ",
            "    WHEN time_to_arrive >= 1500 AND time_to_arrive < 1800 THEN '25-30分钟' ",
            "    WHEN time_to_arrive >= 1800 THEN '30分钟以上' ",
            "    ELSE '其他' ",
            "  END AS time_range, ",
            "  SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) AS trappedArrivalCount, ",
            "  SUM(CASE WHEN order_type = 2 THEN 1 ELSE 0 END) AS nonTrappedArrivalCount, ",
            "  CASE ",
            "    WHEN time_to_arrive >= 0 AND time_to_arrive < 300 THEN 1 ",
            "    WHEN time_to_arrive >= 300 AND time_to_arrive < 600 THEN 2 ",
            "    WHEN time_to_arrive >= 600 AND time_to_arrive < 900 THEN 3 ",
            "    WHEN time_to_arrive >= 900 AND time_to_arrive < 1200 THEN 4 ",
            "    WHEN time_to_arrive >= 1200 AND time_to_arrive < 1500 THEN 5 ",
            "    WHEN time_to_arrive >= 1500 AND time_to_arrive < 1800 THEN 6 ",
            "    WHEN time_to_arrive >= 1800 THEN 7 ",
            "    ELSE 8 ",
            "  END AS sort_no ",
            "]]>",
            "FROM work_order ",
            "WHERE ",
            "<![CDATA[",
            "  time_to_arrive IS NOT NULL ",
            "  AND order_type IN (1, 2) ",
            "  AND status = 99 ",
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
    List<TimeConsumptionStatsDTO> getArriveTimeConsumptionStats1(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "  time_ranges.time_range, ",
            "  COALESCE(stats.trappedArrivalCount, 0) AS trappedArrivalCount, ",
            "  COALESCE(stats.nonTrappedArrivalCount, 0) AS nonTrappedArrivalCount ",
            "FROM (",
            "  SELECT '0-5分钟' AS time_range, 1 AS sort_no ",
            "  UNION ALL SELECT '5-10分钟', 2 ",
            "  UNION ALL SELECT '10-15分钟', 3 ",
            "  UNION ALL SELECT '15-20分钟', 4 ",
            "  UNION ALL SELECT '20-25分钟', 5 ",
            "  UNION ALL SELECT '25-30分钟', 6 ",
            "  UNION ALL SELECT '30分钟以上', 7 ",
            ") time_ranges ",
            "LEFT JOIN (",
            "  SELECT ",
            "<![CDATA[",
            "    CASE ",
            "      WHEN time_to_arrive >= 0 AND time_to_arrive < 300 THEN '0-5分钟' ",
            "      WHEN time_to_arrive >= 300 AND time_to_arrive < 600 THEN '5-10分钟' ",
            "      WHEN time_to_arrive >= 600 AND time_to_arrive < 900 THEN '10-15分钟' ",
            "      WHEN time_to_arrive >= 900 AND time_to_arrive < 1200 THEN '15-20分钟' ",
            "      WHEN time_to_arrive >= 1200 AND time_to_arrive < 1500 THEN '20-25分钟' ",
            "      WHEN time_to_arrive >= 1500 AND time_to_arrive < 1800 THEN '25-30分钟' ",
            "      WHEN time_to_arrive >= 1800 THEN '30分钟以上' ",
            "      ELSE '总数' ",
            "    END AS time_range, ",
            "    SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) AS trappedArrivalCount, ",
            "    SUM(CASE WHEN order_type = 2 THEN 1 ELSE 0 END) AS nonTrappedArrivalCount ",
            "]]>",
            "  FROM work_order ",
            "  WHERE time_to_arrive IS NOT NULL ",
            "    AND order_type IN (1, 2) ",
            "    AND status = 99 ",
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and !searchDTO.district.isEmpty()'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY ",
            "<![CDATA[",
            "    CASE ",
            "      WHEN time_to_arrive >= 0 AND time_to_arrive < 300 THEN '0-5分钟' ",
            "      WHEN time_to_arrive >= 300 AND time_to_arrive < 600 THEN '5-10分钟' ",
            "      WHEN time_to_arrive >= 600 AND time_to_arrive < 900 THEN '10-15分钟' ",
            "      WHEN time_to_arrive >= 900 AND time_to_arrive < 1200 THEN '15-20分钟' ",
            "      WHEN time_to_arrive >= 1200 AND time_to_arrive < 1500 THEN '20-25分钟' ",
            "      WHEN time_to_arrive >= 1500 AND time_to_arrive < 1800 THEN '25-30分钟' ",
            "      WHEN time_to_arrive >= 1800 THEN '30分钟以上' ",
            "      ELSE '总数' ",
            "    END ",
            "]]>",
            ") stats ON time_ranges.time_range = stats.time_range ",
            "ORDER BY time_ranges.sort_no ASC",
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
            "  AND status = 99 ",
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
            "  time_ranges.time_range, ",
            "  COALESCE(stats.trappedRescueCount, 0) AS trappedRescueCount ",
            "FROM (",
            "  SELECT '0-5分钟' AS time_range, 1 AS sort_no ",
            "  UNION ALL SELECT '5-10分钟', 2 ",
            "  UNION ALL SELECT '10-15分钟', 3 ",
            "  UNION ALL SELECT '15-20分钟', 4 ",
            "  UNION ALL SELECT '20-25分钟', 5 ",
            "  UNION ALL SELECT '25-30分钟', 6 ",
            "  UNION ALL SELECT '30分钟以上', 7 ",
            ") time_ranges ",
            "LEFT JOIN (",
            "  SELECT ",
            "<![CDATA[",
            "    CASE ",
            "      WHEN rescue_duration >= 0 AND rescue_duration < 300 THEN '0-5分钟' ",
            "      WHEN rescue_duration >= 300 AND rescue_duration < 600 THEN '5-10分钟' ",
            "      WHEN rescue_duration >= 600 AND rescue_duration < 900 THEN '10-15分钟' ",
            "      WHEN rescue_duration >= 900 AND rescue_duration < 1200 THEN '15-20分钟' ",
            "      WHEN rescue_duration >= 1200 AND rescue_duration < 1500 THEN '20-25分钟' ",
            "      WHEN rescue_duration >= 1500 AND rescue_duration < 1800 THEN '25-30分钟' ",
            "      WHEN rescue_duration >= 1800 THEN '30分钟以上' ",
            "      ELSE '总数' ",
            "    END AS time_range, ",
            "    SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) AS trappedRescueCount ",
            "]]>",
            "  FROM work_order ",
            "  WHERE rescue_duration IS NOT NULL ",
            "    AND order_type = 1 ",
            "    AND status = 99 ",
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and !searchDTO.district.isEmpty()'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY ",
            "<![CDATA[",
            "    CASE ",
            "      WHEN rescue_duration >= 0 AND rescue_duration < 300 THEN '0-5分钟' ",
            "      WHEN rescue_duration >= 300 AND rescue_duration < 600 THEN '5-10分钟' ",
            "      WHEN rescue_duration >= 600 AND rescue_duration < 900 THEN '10-15分钟' ",
            "      WHEN rescue_duration >= 900 AND rescue_duration < 1200 THEN '15-20分钟' ",
            "      WHEN rescue_duration >= 1200 AND rescue_duration < 1500 THEN '20-25分钟' ",
            "      WHEN rescue_duration >= 1500 AND rescue_duration < 1800 THEN '25-30分钟' ",
            "      WHEN rescue_duration >= 1800 THEN '30分钟以上' ",
            "      ELSE '总数' ",
            "    END ",
            "]]>",
            ") stats ON time_ranges.time_range = stats.time_range ",
            "ORDER BY time_ranges.sort_no ASC",
            "</script>"
    })
    List<TimeConsumptionStatsDTO> getRescueTimeConsumptionStats(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "<![CDATA[",
            "  CASE ",
            "    WHEN rescue_duration >= 0 AND rescue_duration < 300 THEN '0-5分钟' ",
            "    WHEN rescue_duration >= 300 AND rescue_duration < 600 THEN '5-10分钟' ",
            "    WHEN rescue_duration >= 600 AND rescue_duration < 900 THEN '10-15分钟' ",
            "    WHEN rescue_duration >= 900 AND rescue_duration < 1200 THEN '15-20分钟' ",
            "    WHEN rescue_duration >= 1200 AND rescue_duration < 1500 THEN '20-25分钟' ",
            "    WHEN rescue_duration >= 1500 AND rescue_duration < 1800 THEN '25-30分钟' ",
            "    WHEN rescue_duration >= 1800 THEN '30分钟以上' ",
            "    ELSE '其他' ",
            "  END AS time_range, ",
            "  SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) AS trappedRescueCount, ",
            "  CASE ",
            "    WHEN rescue_duration >= 0 AND rescue_duration < 300 THEN 1 ",
            "    WHEN rescue_duration >= 300 AND rescue_duration < 600 THEN 2 ",
            "    WHEN rescue_duration >= 600 AND rescue_duration < 900 THEN 3 ",
            "    WHEN rescue_duration >= 900 AND rescue_duration < 1200 THEN 4 ",
            "    WHEN rescue_duration >= 1200 AND rescue_duration < 1500 THEN 5 ",
            "    WHEN rescue_duration >= 1500 AND rescue_duration < 1800 THEN 6 ",
            "    WHEN rescue_duration >= 1800 THEN 7 ",
            "    ELSE 8 ",
            "  END AS sort_no ",
            "]]>",
            "FROM work_order ",
            "WHERE ",
            "<![CDATA[",
            "  rescue_duration IS NOT NULL ",
            "  AND order_type = 1 ",
            "  AND status = 99 ",
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
    List<TimeConsumptionStatsDTO> getRescueTimeConsumptionStats2(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT COUNT(*) AS trappedRescueCount ",
            "FROM work_order ",
            "WHERE rescue_duration IS NOT NULL ",
            "  AND order_type = 1 ",  // 困人工单
            "  AND status = 99 ",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "</script>"
    })
    Integer getTrappedRescueCount(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 获取到达时间超过30分钟的工单
     *
     * @param searchDTO
     * @return
     */
    @Select({
            "<script>",
            "SELECT ",
            "  alarm_time AS time, ",
            "  register_code AS registerCode, ",
            "  maintenance_unit AS maintenanceUnitName, ",
            "  using_unit AS usingUnit, ",
            "  district AS district, ",
            "  project_name AS projectName, ",
            "  ROUND(time_to_arrive/60.0, 2) AS timeToArrive, ",
            "  ROUND(GREATEST((time_to_arrive - 1800)/60.0, 0), 2) AS overtime ",
            "FROM work_order ",
            "WHERE time_to_arrive IS NOT NULL ",
            "  AND alarm_time IS NOT NULL ",
            "  AND status = 99 ",
            "  AND time_to_arrive > 1800 ",  // 30分钟 = 1800秒
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND alarm_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "ORDER BY alarm_time DESC",
            "</script>"
    })
    List<OvertimeWorkOrderDTO> getOvertimeWorkOrders(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "  w.district AS district, ",
            "  w.rescue_code AS rescueCode, ",
            "  w.register_code AS registerCode, ",
            "  DATE(w.alarm_time) AS date, ",
            "  w.alarm_time AS alarmTime, ",
            "  CASE w.order_type ",
            "    WHEN 1 THEN '困人事件' ",
            "    WHEN 2 THEN '故障事件' ",
            "    WHEN 3 THEN '投诉' ",
            "    WHEN 4 THEN '咨询' ",
            "    ELSE '未知' ",
            "  END AS eventType, ",
            "  w.elevator_address AS elevatorAddress, ",
            "  w.maintenance_unit AS maintenanceUnit, ",
            "  w.using_unit AS usingUnit ",
            "FROM work_order w ",
            "WHERE w.rescue_code IN ( ",
            "  SELECT rescue_code ",
            "  FROM work_order ",
            "  WHERE  rescue_code is not null AND rescue_code != ''",
            "    AND status = 99 ",
            "    AND order_type NOT IN (5, 6) ",  // 排除自行脱困和误报工单
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY rescue_code ",
            "  HAVING COUNT(*) >= 2 ",
            ") ",
            "ORDER BY w.rescue_code",
            "</script>"
    })
    List<SecondaryFaultStatsDTO> getSecondaryFaultStats(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 根据区县统计电梯数量及故障数量
     *
     * @param searchDTO
     * @return
     */
    @Select({
            "<script>",
            "SELECT ",
            "  d.district_name AS district, ",
            "  COALESCE(e.elevator_count, 0) AS elevatorCount, ",
            "  COALESCE(w.total_faults, 0) AS totalFaults, ",
            "  COALESCE(w.trapped_mechanical_faults, 0) AS trappedMechanicalFaults, ",
            "  COALESCE(w.trapped_non_mechanical_faults, 0) AS trappedNonMechanicalFaults, ",
            "  COALESCE(w.non_trapped_mechanical_faults, 0) AS nonTrappedMechanicalFaults, ",
            "  COALESCE(w.non_trapped_non_mechanical_faults, 0) AS nonTrappedNonMechanicalFaults, ",
            "  COALESCE(w.other_faults, 0) AS otherFaults, ",
            "  COALESCE(w.casualty_count, 0) AS casualtyCount ",
            "FROM sys_district d ",
            "LEFT JOIN (",
            "  SELECT ",
            "    district, ",
            "    COUNT(*) AS elevator_count ",
            "  FROM elevator ",
            "  GROUP BY district",
            ") e ON d.district_name = e.district ",
            "LEFT JOIN (",
            "  SELECT ",
            "    district, ",
            "    COUNT(*) AS total_faults, ",
            "    SUM(CASE WHEN order_type = 1 AND is_mechanical_failure = true THEN 1 ELSE 0 END) AS trapped_mechanical_faults, ",
            "    SUM(CASE WHEN order_type = 1 AND is_mechanical_failure = false THEN 1 ELSE 0 END) AS trapped_non_mechanical_faults, ",
            "    SUM(CASE WHEN order_type = 2 AND is_mechanical_failure = true THEN 1 ELSE 0 END) AS non_trapped_mechanical_faults, ",
            "    SUM(CASE WHEN order_type = 2 AND is_mechanical_failure = false THEN 1 ELSE 0 END) AS non_trapped_non_mechanical_faults, ",
            "    SUM(CASE WHEN order_type IN (5, 6) THEN 1 ELSE 0 END) AS other_faults, ",
            "    SUM(COALESCE(injured_count, 0) + COALESCE(suspected_death_count, 0)) AS casualty_count ",
            "  FROM work_order ",
            "  WHERE order_type IN (1, 2, 5, 6) ",
            "    AND status = 99 ",
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "  GROUP BY district",
            ") w ON d.district_name = w.district ",
            "WHERE d.is_enabled = 1 ",  // 只查询启用的区域
            "ORDER BY d.sort, d.district_name",
            "</script>"
    })
    List<DistrictStatisticsDTO> getDistrictStatistics2(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "  d.district_name AS district, ",
            "  COALESCE(e.elevator_count, 0) AS elevatorCount, ",
            "  COALESCE(w.total_faults, 0) AS totalFaults, ",
            "  COALESCE(w.trapped_mechanical_faults, 0) AS trappedMechanicalFaults, ",
            "  COALESCE(w.trapped_non_mechanical_faults, 0) AS trappedNonMechanicalFaults, ",
            "  COALESCE(w.non_trapped_mechanical_faults, 0) AS nonTrappedMechanicalFaults, ",
            "  COALESCE(w.non_trapped_non_mechanical_faults, 0) AS nonTrappedNonMechanicalFaults, ",
            "  COALESCE(w.other_faults, 0) AS otherFaults, ",
            "  COALESCE(w.casualty_count, 0) AS casualtyCount ",
            "FROM sys_district d ",
            "LEFT JOIN (",
            "  SELECT ",
            "    district, ",
            "    COUNT(*) AS elevator_count ",
            "  FROM elevator ",
            "  WHERE 1=1 ",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY district",
            ") e ON d.district_name = e.district ",
            "LEFT JOIN (",
            "  SELECT ",
            "    district, ",
            "    COUNT(*) AS total_faults, ",
            "    SUM(CASE WHEN order_type = 1 AND is_mechanical_failure = true THEN 1 ELSE 0 END) AS trapped_mechanical_faults, ",
            "    SUM(CASE WHEN order_type = 1 AND is_mechanical_failure = false THEN 1 ELSE 0 END) AS trapped_non_mechanical_faults, ",
            "    SUM(CASE WHEN order_type = 2 AND is_mechanical_failure = true THEN 1 ELSE 0 END) AS non_trapped_mechanical_faults, ",
            "    SUM(CASE WHEN order_type = 2 AND is_mechanical_failure = false THEN 1 ELSE 0 END) AS non_trapped_non_mechanical_faults, ",
            "    SUM(CASE WHEN order_type IN (5, 6) THEN 1 ELSE 0 END) AS other_faults, ",
            "    SUM(COALESCE(injured_count, 0) + COALESCE(suspected_death_count, 0)) AS casualty_count ",
            "  FROM work_order ",
            "  WHERE order_type IN (1, 2, 5, 6) ",
            "    AND status = 99 ",
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY district",
            ") w ON d.district_name = w.district ",
            "WHERE d.is_enabled = 1 ",  // 只查询启用的区域
            "  <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "    AND d.district_name = #{searchDTO.district}",
            "  </if>",
            "ORDER BY d.sort, d.district_name",
            "</script>"
    })
    List<DistrictStatisticsDTO> getDistrictStatistics(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 按维保单位统计电梯数、故障数和故障率
     *
     * @param searchDTO 包含开始时间、结束时间和区县的搜索条件
     * @return 维保单位统计数据列表
     */
    @Select({
            "<script>",
            "SELECT ",
            "  ROW_NUMBER() OVER (ORDER BY m.maintenance_unit) AS idx, ",
            "  m.maintenance_unit AS maintenanceUnit, ",
            "  COALESCE(m.elevator_count, 0) AS elevatorCount, ",
            "  COALESCE(w.fault_count, 0) AS faultCount, ",
            "  CASE ",
            "    WHEN m.elevator_count > 0 THEN ",
            "      CONCAT(ROUND(COALESCE(w.fault_count, 0) * 100.0 / m.elevator_count, 2), '%') ",
            "    ELSE '0.00%' ",
            "  END AS faultRate ",
            "FROM (",
            "  SELECT ",
            "    maintenance_unit, ",
            "    COUNT(*) AS elevator_count ",
            "  FROM elevator ",
            "  WHERE maintenance_unit IS NOT NULL AND maintenance_unit != '' ",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY maintenance_unit",
            ") m ",
            "LEFT JOIN (",
            "  SELECT ",
            "    maintenance_unit, ",
            "    COUNT(*) AS fault_count ",
            "  FROM work_order ",
            "  WHERE status = 99 ",
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY maintenance_unit",
            ") w ON m.maintenance_unit = w.maintenance_unit ",
            "WHERE w.fault_count IS NOT NULL ",
            "ORDER BY m.maintenance_unit",
            "</script>"
    })
    List<MaintenanceUnitFaultRateDTO> getMaintenanceUnitFaultRate(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 按使用单位统计电梯数、故障数和故障率
     *
     * @param searchDTO 包含开始时间、结束时间和区县的搜索条件
     * @return 使用单位统计数据列表
     */
    @Select({
            "<script>",
            "SELECT ",
            "  ROW_NUMBER() OVER (ORDER BY u.district, u.using_unit) AS idx, ",
            "  u.using_unit AS usingUnit, ",
            "  u.district AS district, ",
            "  COALESCE(u.elevator_count, 0) AS elevatorCount, ",
            "  COALESCE(w.fault_count, 0) AS faultCount, ",
            "  CASE ",
            "    WHEN u.elevator_count > 0 THEN ",
            "      CONCAT(ROUND(COALESCE(w.fault_count, 0) * 100.0 / u.elevator_count, 2), '%') ",
            "    ELSE '0.00%' ",
            "  END AS faultRate ",
            "FROM (",
            "  SELECT ",
            "    using_unit, ",
            "    district, ",
            "    COUNT(*) AS elevator_count ",
            "  FROM elevator ",
            "  WHERE using_unit IS NOT NULL AND using_unit != '' ",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY using_unit, district",
            ") u ",
            "LEFT JOIN (",
            "  SELECT ",
            "    e.using_unit, ",
            "    e.district, ",
            "    COUNT(*) AS fault_count ",
            "  FROM work_order wo ",
            "  JOIN elevator e ON wo.rescue_code = e.rescue_code ",
            "  WHERE wo.status = 99 ",
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND wo.create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND e.district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY e.using_unit, e.district",
            ") w ON u.using_unit = w.using_unit AND u.district = w.district ",
            "WHERE w.fault_count IS NOT NULL ",
            "ORDER BY u.district, u.using_unit",
            "</script>"
    })
    List<UsingUnitFaultRateDTO> getUsingUnitFaultRate(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 按电梯品牌统计电梯数、故障数和故障率
     *
     * @param searchDTO 包含开始时间、结束时间和区县的搜索条件
     * @return 电梯品牌统计数据列表
     */
    @Select({
            "<script>",
            "SELECT ",
            "  ROW_NUMBER() OVER (ORDER BY b.brand_name) AS idx, ",
            "  b.brand_name AS brand, ",
            "  COALESCE(b.elevator_count, 0) AS elevatorCount, ",
            "  COALESCE(w.fault_count, 0) AS faultCount, ",
            "  CASE ",
            "    WHEN b.elevator_count > 0 THEN ",
            "      CONCAT(ROUND(COALESCE(w.fault_count, 0) * 100.0 / b.elevator_count, 2), '%') ",
            "    ELSE '0.00%' ",
            "  END AS faultRate ",
            "FROM (",
            "  SELECT ",
            "    brand AS brand_name, ",
            "    COUNT(*) AS elevator_count ",
            "  FROM elevator ",
            "  WHERE brand IS NOT NULL AND brand != '' ",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY brand",
            ") b ",
            "LEFT JOIN (",
            "  SELECT ",
            "    e.brand, ",
            "    COUNT(*) AS fault_count ",
            "  FROM work_order wo ",
            "  JOIN elevator e ON wo.rescue_code = e.rescue_code ",  // 通过救援码关联
            "  WHERE wo.status = 99 ",  // 工单状态为终结
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND wo.create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND e.district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY e.brand",
            ") w ON b.brand_name = w.brand ",
            "ORDER BY b.brand_name",
            "</script>"
    })
    List<ElevatorBrandFaultRateDTO> getElevatorBrandFaultRate(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "<![CDATA[",
            "  CASE ",
            "    WHEN e.use_years <= 5 THEN '5年以下（含5年）' ",
            "    WHEN e.use_years <= 10 THEN '5-10年以下（含10年）' ",
            "    WHEN e.use_years <= 15 THEN '10-15年以下（含15年）' ",
            "    WHEN e.use_years > 15 THEN '15年以上' ",
            "    ELSE '未知年限电梯' ",
            "  END AS ageRange, ",
            "]]>",
            "  COUNT(DISTINCT e.rescue_code) AS elevatorCount, ",
            "  COUNT(w.id) AS faultCount, ",
            "  SUM(CASE WHEN w.order_type = 1 THEN 1 ELSE 0 END) AS trappedFaultCount, ",
            "  SUM(CASE WHEN w.order_type = 2 THEN 1 ELSE 0 END) AS nonTrappedFaultCount, ",
            "  SUM(CASE WHEN w.order_type IN (5, 6) THEN 1 ELSE 0 END) AS otherFaultCount, ",
            "  CASE ",
            "    WHEN COUNT(DISTINCT e.rescue_code) > 0 THEN ",
            "      CONCAT(ROUND((COUNT(w.id) * 100.0 / COUNT(DISTINCT e.rescue_code)), 2), '%') ",
            "    ELSE '0.00%' ",
            "  END AS faultRate ",
            "FROM ( ",
            "  SELECT  ",
            "    rescue_code, ",
            "    district, ",
            "    TIMESTAMPDIFF(YEAR, operation_start_date, CURDATE()) AS use_years ",
            "  FROM elevator ",
            "  WHERE operation_start_date IS NOT NULL ",
            "    AND rescue_code IS NOT NULL ",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            ") e ",
            "LEFT JOIN work_order w  ",
            "  ON e.rescue_code = w.rescue_code ",
            "  AND w.status = 99 ",
            "  AND w.order_type IN (1, 2, 5, 6) ",
            "  <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "    AND w.create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "  </if>",
            "GROUP BY  ",
            "<![CDATA[",
            "  CASE ",
            "    WHEN e.use_years <= 5 THEN '5年以下（含5年）' ",
            "    WHEN e.use_years <= 10 THEN '5-10年以下（含10年）' ",
            "    WHEN e.use_years <= 15 THEN '10-15年以下（含15年）' ",
            "    WHEN e.use_years > 15 THEN '15年以上' ",
            "    ELSE '未知年限电梯' ",
            "  END ",
            "]]>",
            "ORDER BY  ",
            "  MIN(e.use_years)",
            "</script>"
    })
    List<ElevatorAgeStatisticsDTO> getElevatorAgeStatistics(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 按工单类型统计各类工单的数量及占比
     *
     * @param searchDTO 查询条件，包括开始时间、结束时间和区县
     * @return 工单类型统计结果列表
     */
    @Select({
            "<script>",
            "SELECT ",
            "  order_type, ",
            "  COUNT(*) AS typeCount, ",
            "  ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM work_order WHERE status = 99 ",
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  ), 2) AS percentage ",
            "FROM work_order ",
            "WHERE status = 99 ",
            "  <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "    AND create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "  </if>",
            "  <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "    AND district = #{searchDTO.district}",
            "  </if>",
            "GROUP BY order_type ",
            "ORDER BY order_type",
            "</script>"
    })
    List<OrderTypeStatisticsDTO> getOrderTypeStatistics(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 查询各品牌故障数和总故障数
     *
     * @param searchDTO 包含开始时间、结束时间的搜索条件
     * @return 各品牌故障数及总故障数列表
     */
    @Select({
            "<script>",
            "SELECT ",
            "  ROW_NUMBER() OVER (ORDER BY w.fault_count DESC) AS idx, ",
            "  b.brand_name AS brand, ",
            "  COALESCE(w.fault_count, 0) AS faultCount, ",
            "  (SELECT COUNT(*) FROM work_order wo ",
            "   JOIN elevator e ON wo.rescue_code = e.rescue_code ",
            "   WHERE wo.status = 99 ",
            "     <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "       AND wo.create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "     </if>",
            "  ) AS totalFaultCount ",
            "FROM (",
            "  SELECT brand AS brand_name",
            "  FROM elevator",
            "  WHERE brand IS NOT NULL AND brand != ''",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY brand",
            ") b ",
            "JOIN (",
            "  SELECT e.brand, COUNT(*) AS fault_count",
            "  FROM work_order wo",
            "  JOIN elevator e ON wo.rescue_code = e.rescue_code",
            "  WHERE wo.status = 99",
            "    <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "      AND wo.create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "    </if>",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND e.district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY e.brand",
            ") w ON b.brand_name = w.brand ",
            "WHERE w.fault_count > 0 ",
            "ORDER BY w.fault_count DESC",
            "</script>"
    })
    List<ElevatorBrandFaultRateDTO> getHighFaultRateBrands(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 查询同一电梯发生四次以上故障的统计
     *
     * @param searchDTO 包含开始时间、结束时间的搜索条件
     * @return 故障次数>=4的电梯列表
     */
    @Select({
            "<script>",
            "SELECT ",
            "  e.rescue_code AS rescueCode, ",
            "  e.using_unit AS usingUnit, ",
            "  e.maintenance_unit AS maintenanceUnit, ",
            "  e.brand AS brand, ",
            "  e.district AS district, ",
            "  COUNT(*) AS faultCount ",
            "FROM work_order wo ",
            "JOIN elevator e ON wo.rescue_code = e.rescue_code ",
            "WHERE wo.status = 99 ",
            "  <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "    AND wo.create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "  </if>",
            "  <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "    AND e.district = #{searchDTO.district}",
            "  </if>",
            "GROUP BY e.rescue_code, e.using_unit, e.maintenance_unit, e.brand, e.district ",
            "HAVING COUNT(*) >= 4 ",
            "ORDER BY faultCount DESC",
            "</script>"
    })
    List<RepeatedFaultElevatorDTO> getRepeatedFaultElevators(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 通过维保单位查询时间范围内故障或困人工单对应的fault_records中的sub_code值（去重）
     *
     * @param maintenanceUnit 维保单位名称
     * @param searchDTO 包含开始时间、结束时间、区县的搜索条件
     * @return sub_code列表（去重）
     */
    @Select({
            "<script>",
            "SELECT DISTINCT fr.sub_code ",
            "FROM fault_records fr ",
            "INNER JOIN work_order wo ON fr.order_no = wo.order_no ",
            "WHERE wo.status = 99 ",
            "  AND wo.order_type IN (1, 2) ",  // 1:困人, 2:故障
            "  AND wo.maintenance_unit = #{maintenanceUnit} ",
            "  <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "    AND wo.district = #{searchDTO.district}",
            "  </if>",
            "  <if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "    AND wo.create_time BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "  </if>",
            "ORDER BY fr.sub_code",
            "</script>"
    })
    List<String> getFaultSubCodesByMaintenanceUnit(@Param("maintenanceUnit") String maintenanceUnit, @Param("searchDTO") SearchDTO searchDTO);

}