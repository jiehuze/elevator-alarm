package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.WorkOrderProgressMapper;
import com.schedule.elevator.entity.WorkOrderProgress;
import com.schedule.elevator.service.IWorkOrderProgressService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class WorkOrderProgressServiceImpl extends ServiceImpl<WorkOrderProgressMapper, WorkOrderProgress>
        implements IWorkOrderProgressService {
    @Override
    public List<WorkOrderProgress> queryByOrderNo(String orderNo) {
        LambdaQueryWrapper<WorkOrderProgress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkOrderProgress::getOrderNo, orderNo);
        return list(queryWrapper);
    }

    @Override
    public HashMap<Integer, WorkOrderProgress> queryMapByOrderNo(String orderNo) {
        LambdaQueryWrapper<WorkOrderProgress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkOrderProgress::getOrderNo, orderNo);
        List<WorkOrderProgress> wps = list(queryWrapper);
        HashMap<Integer, WorkOrderProgress> wpMap = new HashMap<>();
        for (WorkOrderProgress wp : wps) {
            wpMap.put(wp.getStatus(), wp);
        }

        return wpMap;
    }
}