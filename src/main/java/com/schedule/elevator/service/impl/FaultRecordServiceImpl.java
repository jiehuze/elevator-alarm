package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.IFaultRecordMapper;
import com.schedule.elevator.entity.FaultRecord;
import com.schedule.elevator.service.IFaultRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class FaultRecordServiceImpl extends ServiceImpl<IFaultRecordMapper, FaultRecord>
        implements IFaultRecordService {

    @Override
    public List<FaultRecord> getByOrderNo(String orderNo) {
        LambdaQueryWrapper<FaultRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRecord::getOrderNo, orderNo);
        return this.list(wrapper);
    }

    @Override
    public List<FaultRecord> getByRootCode(String rootCode) {
        LambdaQueryWrapper<FaultRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRecord::getRootCode, rootCode);
        return this.list(wrapper);
    }

    @Override
    public boolean removeByOrderNo(String orderNo) {
        LambdaQueryWrapper<FaultRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRecord::getOrderNo, orderNo);
        return this.remove(wrapper);
    }

    @Override
    public List<Map<String, Object>> countByRootCodeInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.countByRootCodeInTimeRange(startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> countBySubCodeInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.countBySubCodeInTimeRange(startTime, endTime);
    }
}
