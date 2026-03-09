package com.schedule.excel;

import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.enums.ProjectTypeEnum;
import com.schedule.elevator.enums.WorkOrderStatusEnum;
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
        System.out.println("----------------workorder: " + entity);
        dto.setLocation(ProjectTypeEnum.getByCode(entity.getProjectType()).getDescription());
        dto.setRegisterCode(entity.getRegisterCode());
        dto.setRescueCode(entity.getRescueCode());
        dto.setUsingUnit(entity.getUsingUnit());
        dto.setMaintenanceUnit(entity.getMaintenanceUnit());
        dto.setProjectName(entity.getProjectName());
        dto.setElevatorName(entity.getElevatorName());
        dto.setDistrict(entity.getDistrict());
        dto.setFaultReason(entity.getFailureReason());
        dto.setIsMedicalRescueStarted(entity.getMedicalRescueStarted() == true ? "是" : "否");
        dto.setIsMajorIncidentReported(entity.getMajorIncident() == true ? "是" : "否");
        dto.setTrappedCount(entity.getTrappedCount());
        dto.setInjuredCount(entity.getInjuredCount());
        dto.setSuspectedDeathCount(entity.getSuspectedDeathCount());

        dto.setAlarmPersonName(entity.getAlarmPersonName());
        dto.setAlarmPersonPhone(entity.getAlarmPersonPhone());
        dto.setMaintenancePersonnelName(entity.getMaintenancePersonnelName());
        dto.setMaintenancePersonnelPhone(entity.getMaintenancePersonnelPhone());

        dto.setAlarmTime(entity.getCreateTime());

        dto.setDispatchTime(dispatchTime);
        dto.setArrivalTime(arriveTime);
        dto.setRescueTime(rescueTime);
        dto.setFollowUpTime(followUpTime);
        dto.setCloseTime(closeTime);

        return dto;
    }

    public static WorkOrder toEntity(WorkOrderExcel dto) {
        if (dto == null) return null;
        WorkOrder entity = new WorkOrder();
        entity.setOrderNo(dto.getOrderNo());
        entity.setStatus(WorkOrderStatusEnum.CLOSED.getCode());
        System.out.println("----------------workorder: " + dto.getOrderType());
        entity.setOrderType(WorkOrderTypeEnum.getByDescription(dto.getOrderType()).getCode());
        entity.setProjectType(ProjectTypeEnum.getByDescription(dto.getLocation()).getCode());
        entity.setRegisterCode(dto.getRegisterCode());
        entity.setRescueCode(dto.getRescueCode());
        entity.setUsingUnit(dto.getUsingUnit());
        entity.setMaintenanceUnit(dto.getMaintenanceUnit());
        entity.setProjectName(dto.getProjectName());
        entity.setElevatorName(dto.getElevatorName());
        entity.setDistrict(dto.getDistrict());
        entity.setFailureReason(dto.getFaultReason());
        if (dto.getIsMedicalRescueStarted() != null) {
            entity.setMedicalRescueStarted(dto.getIsMedicalRescueStarted().equals("是"));
        }
        if (dto.getIsMajorIncidentReported() != null) {
            entity.setMajorIncident(dto.getIsMajorIncidentReported().equals("是"));
        }
        entity.setTrappedCount(dto.getTrappedCount());
        entity.setInjuredCount(dto.getInjuredCount());
        entity.setSuspectedDeathCount(dto.getSuspectedDeathCount());
        entity.setAlarmPersonName(dto.getAlarmPersonName());
        entity.setAlarmPersonPhone(dto.getAlarmPersonPhone());
        entity.setMaintenancePersonnelName(dto.getMaintenancePersonnelName());
        entity.setMaintenancePersonnelPhone(dto.getMaintenancePersonnelPhone());
        entity.setCreateTime(dto.getAlarmTime());
        entity.setUpdateTime(LocalDateTime.now());
        return entity;
    }
}
