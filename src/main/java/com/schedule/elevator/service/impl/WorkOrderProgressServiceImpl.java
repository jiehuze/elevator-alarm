package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.WorkOrderProgressMapper;
import com.schedule.elevator.entity.WorkOrderProgress;
import com.schedule.elevator.service.IWorkOrderProgressService;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderProgressServiceImpl extends ServiceImpl<WorkOrderProgressMapper, WorkOrderProgress>
        implements IWorkOrderProgressService {
}