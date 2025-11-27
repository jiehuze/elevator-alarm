package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.WorkOrderTraceMapper;
import com.schedule.elevator.entity.WorkOrderTrace;
import com.schedule.elevator.service.IWorkOrderTraceService;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderTraceServiceImpl extends ServiceImpl<WorkOrderTraceMapper, WorkOrderTrace>
        implements IWorkOrderTraceService {
}