package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.WorkOrderScreenshotMapper;
import com.schedule.elevator.entity.WorkOrderScreenshot;
import com.schedule.elevator.service.IWorkOrderScreenshotService;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderScreenshotServiceImpl
        extends ServiceImpl<WorkOrderScreenshotMapper, WorkOrderScreenshot>
        implements IWorkOrderScreenshotService {
}