package com.schedule.excel;

import com.schedule.elevator.entity.ElevatorInfo;

public class ElevatorInfoExcelConverter {
    public static ElevatorInfoTemplateExcel toDto(ElevatorInfo entity) {
        ElevatorInfoTemplateExcel dto = new ElevatorInfoTemplateExcel();
        dto.setElevatorNo(entity.getElevatorNo());
        dto.setProvince(entity.getProvince());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setLocation(entity.getLocation());
        dto.setOrganization(entity.getOrganization());
        dto.setManagerName(entity.getManagerName());
        dto.setManagerTitle(entity.getManagerTitle());
        dto.setDescription(entity.getDescription());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setManagerPhone(entity.getManagerPhone());
        dto.setBackupContact(entity.getBackupContact());
        dto.setBackupPhone(entity.getBackupPhone());
        return dto;
    }

    public static ElevatorInfo toEntity(ElevatorInfoTemplateExcel dto) {
        ElevatorInfo entity = new ElevatorInfo();
        entity.setElevatorNo(dto.getElevatorNo());
        entity.setProvince(dto.getProvince());
        entity.setCity(dto.getCity());
        entity.setDistrict(dto.getDistrict());
        entity.setLocation(dto.getLocation());
        entity.setOrganization(dto.getOrganization());
        entity.setManagerName(dto.getManagerName());
        entity.setManagerTitle(dto.getManagerTitle());
        entity.setDescription(dto.getDescription());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setManagerPhone(dto.getManagerPhone());
        entity.setBackupContact(dto.getBackupContact());
        entity.setBackupPhone(dto.getBackupPhone());
        // createdAt / updatedAt 由数据库自动填充
        return entity;
    }
}
