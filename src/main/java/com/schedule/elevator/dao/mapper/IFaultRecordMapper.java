package com.schedule.elevator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
            "    root_code as faultCode, " +
            "    COUNT(order_no) as faultCount " +
            "FROM fault_records " +
            "WHERE created_at BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY root_code " +
            "ORDER BY faultCount DESC" +
            "</script>")
    List<Map<String, Object>> countByRootCodeInTimeRange(@Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 按子故障码统计在指定时间范围内的故障数量（相同工单号只计算一次）
     */
    @Select("<script>" +
            "SELECT " +
            "    sub_code as faultCode, " +
            "    COUNT(order_no) as faultCount " +
            "FROM fault_records " +
            "WHERE created_at BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY sub_code " +
            "ORDER BY faultCount DESC" +
            "</script>")
    List<Map<String, Object>> countBySubCodeInTimeRange(@Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

}
