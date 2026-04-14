package com.schedule.elevator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.FaultRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface IFaultRecordMapper extends BaseMapper<FaultRecord> {
    /**
     * 按根故障码统计在指定时间范围内的故障数量（相同工单号只计算一次）
     */
    @Select("<script>" +
            "SELECT " +
            "    f.root_code as faultCode, " +
            "    COUNT(f.order_no) as faultCount " +
            "FROM fault_records f " +
            "JOIN work_order w ON f.order_no = w.order_no " +
            "<where>" +
            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>" +
            "AND f.created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if>" +
            "<if test='searchDTO.district != null and searchDTO.district != \"\"'>" +
            "AND w.district = #{searchDTO.district} " +
            "</if>" +
            "<if test='searchDTO != null and searchDTO.maintenanceUnitId != null'>" +
            "  AND w.maintenance_unit_id = #{searchDTO.maintenanceUnitId}" +
            "</if>" +
            "</where>" +
            "GROUP BY f.root_code " +
            "ORDER BY faultCount DESC" +
            "</script>")
    List<Map<String, Object>> countByRootCodeInTimeRange(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 按子故障码统计在指定时间范围内的故障数量（相同工单号只计算一次）
     */
    @Select("<script>" +
            "SELECT " +
            "    f.sub_code as faultCode, " +
            "    COUNT(f.order_no) as faultCount " +
            "FROM fault_records f " +
            "JOIN work_order w ON f.order_no = w.order_no " +
            "<where>" +
            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>" +
            "AND f.created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if>" +
            "<if test='searchDTO.district != null and searchDTO.district != \"\"'>" +
            "AND w.district = #{searchDTO.district} " +
            "</if>" +
            "<if test='searchDTO != null and searchDTO.maintenanceUnitId != null'>" +
            "  AND w.maintenance_unit_id = #{searchDTO.maintenanceUnitId}" +
            "</if>" +
            "</where>" +
            "GROUP BY f.sub_code " +
            "ORDER BY faultCount DESC" +
            "</script>")
    List<Map<String, Object>> countBySubCodeInTimeRange(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 获取指定时间范围内的故障数量
     *
     * @param searchDTO
     * @return
     */
    @Select({
            "<script>",
            "SELECT ",
            "  COUNT(*) AS faultCount ",
            "FROM fault_records f ",
            "JOIN work_order w ON f.order_no = w.order_no ",
            "WHERE 1=1 ",
            "  <!-- 时间范围筛选 -->",
            "  <if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "    AND f.created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "  </if>",
            "  <!-- 区域筛选 -->",
            "  <if test='searchDTO.district != null and searchDTO.district != \"\"'>",
            "    AND w.district = #{searchDTO.district}",
            "  </if>",
            "  <if test='searchDTO != null and searchDTO.maintenanceUnitId != null'>",
            "    AND w.maintenance_unit_id = #{searchDTO.maintenanceUnitId}",
            "  </if>",
            "</script>"
    })
    Long countFaultRecords(@Param("searchDTO") SearchDTO searchDTO);
}
