package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.entity.FaultRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IFaultRecordService extends IService<FaultRecord> {

    /**
     * 根据工单编号查询故障记录
     */
    List<FaultRecord> getByOrderNo(String orderNo);

    /**
     * 根据根故障码查询记录
     */
    List<FaultRecord> getByRootCode(String rootCode);

    /**
     * 删除指定工单的所有故障记录
     */
    boolean removeByOrderNo(String orderNo);

    /**
     * 按根故障码统计在指定时间范围内的故障数量（相同工单号只计算一次）
     */
    List<Map<String, Object>> countByRootCodeInTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 按根故障码统计在指定时间范围内的故障数量（相同工单号只计算一次）
     */
    List<Map<String, Object>> countBySubCodeInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}
