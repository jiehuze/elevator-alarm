package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.entity.WorkOrderProgress;

import java.util.List;

public interface IWorkOrderProgressService extends IService<WorkOrderProgress> {
    List<WorkOrderProgress> queryByOrderNo(String orderNo);
}