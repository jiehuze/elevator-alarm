package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.HandleProgressDTO;
import com.schedule.elevator.dto.WorkOrderDTO;
import com.schedule.elevator.entity.WorkOrder;

public interface IWorkOrderService extends IService<WorkOrder> {
    Page<WorkOrder> queryByConditionsPage(WorkOrderDTO wrokOrderDTO);

    Boolean createRescueInfo(WorkOrder workOrder);

    Boolean updateStatus(WorkOrder workOrder);

    Boolean handleWorkOrder(HandleProgressDTO handleProgressDTO);
}