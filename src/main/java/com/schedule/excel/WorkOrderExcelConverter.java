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
        if (dto == null) {
            throw new IllegalArgumentException("工单 Excel 数据不能为空");
        }
        WorkOrder entity = new WorkOrder();
        entity.setOrderNo(dto.getOrderNo());
        entity.setStatus(WorkOrderStatusEnum.CLOSED.getCode());
        System.out.println("----------------workorder: " + dto.getOrderType());
        // 处理工单类型
        String orderTypeDesc = dto.getOrderType();
        if (orderTypeDesc == null || orderTypeDesc.trim().isEmpty()) {
            throw new IllegalArgumentException("工单号：" + dto.getOrderNo() + "，工单类型不能为空");
        }
        if (orderTypeDesc != null && orderTypeDesc.contains("工单")) {
            orderTypeDesc = orderTypeDesc.replace("工单", "");
        }
        WorkOrderTypeEnum orderType = WorkOrderTypeEnum.getByDescription(orderTypeDesc.trim());
        if (orderType == null) {
            throw new IllegalArgumentException("工单号：" + dto.getOrderNo() + "，无效的工单类型：" + dto.getOrderType() +
                    "，有效值为：困人、故障、投诉、咨询、自行脱困、误报");
        }
        entity.setOrderType(orderType.getCode());

        // 处理工单子类型（如果有）
        if (dto.getOrderSubType() != null && !dto.getOrderSubType().trim().equals("无")) {
            WorkOrderTypeEnum subType = WorkOrderTypeEnum.getByDescription(dto.getOrderSubType().trim());
            if (subType == null) {
                throw new IllegalArgumentException("工单号：" + dto.getOrderNo() + "，无效的工单子类型：" + dto.getOrderSubType() +
                        "，有效值为：困人、故障、投诉、咨询、自行脱困、误报");
            }
            entity.setOrderType(subType.getCode());
        }

        // 处理项目类型
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("工单号：" + dto.getOrderNo() + "，项目位置类型不能为空");
        }
        ProjectTypeEnum projectType = ProjectTypeEnum.getByDescription(dto.getLocation().trim());
        if (projectType == null) {
            throw new IllegalArgumentException("工单号：" + dto.getOrderNo() + "，无效的项目类型：" + dto.getLocation() +
                    "，有效值为：住宅区、办公楼、商业区、宾馆饭店、医院、学校、交通场所、文体娱场馆、其他");
        }
        entity.setProjectType(projectType.getCode());

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
