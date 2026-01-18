package com.schedule.excel;

import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.enums.ProjectTypeEnum;
import com.schedule.elevator.enums.WorkOrderTypeEnum;

import java.time.LocalDateTime;

public class WorkOrderExcelConverter {
    public static WorkOrderExcel toDto(WorkOrder entity,
                                       LocalDateTime dispatchTime,
                                       LocalDateTime arriveTime,
                                       LocalDateTime rescueTime,
                                       LocalDateTime followUpTime,
                                       LocalDateTime closeTime) {
        if (entity == null) return null;
        WorkOrderExcel dto = new WorkOrderExcel();
        dto.setOrderNo(entity.getOrderNo());
        dto.setOrderType(WorkOrderTypeEnum.getByCode(entity.getOrderType()).getDescription());
        dto.setOrderSubType("无");
        dto.setLocation(ProjectTypeEnum.getByCode(entity.getProjectType()).getDescription());
        dto.setRegisterCode(entity.getRegisterCode());
        dto.setRescueCode(entity.getRescueCode());
        dto.setUsingUnit(entity.getUsingUnit());
        dto.setMaintenanceUnit(entity.getMaintenanceUnit());
        dto.setProjectName(entity.getProjectName());
        dto.setElevatorAddress(entity.getElevatorAddress());
        dto.setFaultReason(entity.getFailureReason());
        dto.setIsMedicalRescueStarted(entity.getMedicalRescueStarted() == true ? "是" : "否");
        dto.setIsMajorIncidentReported(entity.getMajorIncident() == true ? "是" : "否");
        dto.setTrappedCount(entity.getTrappedCount());
        dto.setInjuredCount(entity.getInjuredCount());
        dto.setSuspectedDeathCount(entity.getSuspectedDeathCount());
        dto.setAlarmTime(entity.getCreateTime());

        dto.setDispatchTime(dispatchTime);
        dto.setArrivalTime(arriveTime);
        dto.setRescueTime(rescueTime);
        dto.setFollowUpTime(followUpTime);
        dto.setCloseTime(closeTime);

        return dto;
    }
}
