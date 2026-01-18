package com.schedule.excel;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.schedule.elevator.dto.MaintenanceDTO;
import com.schedule.elevator.entity.*;
import com.schedule.elevator.enums.ElevatorUsageStatusEnum;
import com.schedule.elevator.enums.ProjectTypeEnum;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ElevatorImportExcelConverter {
    /**
     * 将 DTO 转换为 Entity（用于导入保存）
     */
    public static ElevatorInfo toElevatorEntity(ElevatorImportTemplateExcel dto) {
        if (dto == null) return null;

        ElevatorInfo entity = new ElevatorInfo();

        entity.setRescueCode(dto.getRescueCode());
        entity.setRegisterCode(dto.getRegisterCode());
        entity.setElevatorNo(dto.getElevatorNo());
        entity.setElevatorName(dto.getElevatorName());
        entity.setElevatorType(dto.getElevatorType());
        entity.setUsageStatus(ElevatorUsageStatusEnum.getByDescription(dto.getUsageStatus()).getCode());
        entity.setNextInspectionDate(parseDate(dto.getNextInspectionDate()));
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setOperationStartDate(parseDate(dto.getOperationStartDate()));
        entity.setMaintenanceType(dto.getMaintenanceType());
        entity.setMaintenancePersonnelName(dto.getWorkerName());
        entity.setPropertyOwner(dto.getPropertyOwner());
        entity.setFactorySerialNumber(dto.getFactorySerialNumber());
        entity.setInstallationCompany(dto.getInstallationCompany());
        entity.setRenovationDate(parseDate(dto.getRenovationDate()));
        entity.setDriveType(dto.getDriveType());
        entity.setInspectionAgency(dto.getInspectionAgency());
        entity.setRegistrationAuthority(dto.getRegistrationAuthority());
        entity.setRegistrationDate(parseDate(dto.getRegistrationDate()));
        entity.setLocation(dto.getAddress());
        entity.setProvince(dto.getProvince());
        entity.setCity(dto.getCity());
        entity.setDistrict(dto.getDistrict());
        entity.setProjectName(dto.getProjectName());
        entity.setUsingUnit(dto.getUsingUnit());
        entity.setMaintenanceUnit(dto.getMaintenanceUnitName());

        if (StringUtils.isNotBlank(dto.getProjectType()) && ProjectTypeEnum.getByDescription(dto.getProjectType()) != null) {
            entity.setProjectType(ProjectTypeEnum.getByDescription(dto.getProjectType()).getCode());
        }

        return entity;
    }

    public static Community toCommunityEntity(ElevatorImportTemplateExcel dto) {
        if (dto == null) return null;
        Community community = new Community();

        community.setProjectName(dto.getProjectName())
                .setDistrict(dto.getDistrict())
                .setCity(dto.getCity())
                .setRealEstateBrand(dto.getRealEstateBrand())
                .setProvince(dto.getProvince())
                .setAddress(dto.getAddress())
                .setUsingUnit(dto.getUsingUnit())
                .setSafetyOfficerName(dto.getSafetyOfficerName())
                .setSafetyOfficerPhone(dto.getSafetyOfficerPhone());

        if (StringUtils.isNotBlank(dto.getProjectType()) && ProjectTypeEnum.getByDescription(dto.getProjectType()) != null) {
            community.setProjectType(ProjectTypeEnum.getByDescription(dto.getProjectType()).getCode());
        }

        return community;
    }

    /**
     * 将 DTO 转换为 维保单位Entity
     */
    public static MaintenanceDTO toMaintenanceEntity(ElevatorImportTemplateExcel dto) {
        if (dto == null) return null;

        MaintenanceDTO entity = new MaintenanceDTO();
        entity.setMaintenanceUnit(new MaintenanceUnit());
        entity.setMaintenanceTeam(new MaintenanceTeam());
        entity.setMaintenancePersonnel(new MaintenancePersonnel());

        entity.getMaintenanceUnit().setMaintainerUnitName(dto.getMaintenanceUnitName());
        entity.getMaintenanceUnit().setMaintainerUnitManager(dto.getMaintenanceUnitManager());
        entity.getMaintenanceUnit().setMaintainerUnitManagerPhone(dto.getMaintenanceUnitManagerPhone());

        entity.getMaintenanceTeam().setTeamName(dto.getTeamName());
        entity.getMaintenanceTeam().setLeaderName(dto.getTeamLeaderName());
        entity.getMaintenanceTeam().setLeaderPhone(dto.getTeamLeaderPhone());
        entity.getMaintenanceTeam().setProvince(dto.getProvince());
        entity.getMaintenanceTeam().setCity(dto.getCity());
        entity.getMaintenanceTeam().setDistrict(dto.getDistrict());

        entity.getMaintenancePersonnel().setName(dto.getWorkerName());
        entity.getMaintenancePersonnel().setPhone(dto.getWorkerPhone());
        entity.getMaintenancePersonnel().setCompany(dto.getMaintenanceUnitName());
        entity.getMaintenancePersonnel().setStatus(1);

        return entity;
    }

    /**
     * 将 DTO 转换为 使用单位信息Entity
     */
    public static PropertyInfo toPropertyEntity(ElevatorImportTemplateExcel dto) {
        if (dto == null) return null;

        PropertyInfo entity = new PropertyInfo();

        entity.setUsingUnit(dto.getUsingUnit());
        entity.setUsingUnitManager(dto.getUsingUnitManager());
        entity.setUsingUnitManagerPhone(dto.getUsingUnitManagerPhone());

        return entity;
    }

    /**
     * 将 Entity 转换为 DTO（用于导出或返回前端）
     */
    public static ElevatorImportTemplateExcel toDTO(ElevatorInfo entity,
                                                    Community community,
                                                    PropertyInfo propertyInfo,
                                                    MaintenanceUnit maintenanceUnit,
                                                    MaintenanceTeam maintenanceTeam,
                                                    MaintenancePersonnel maintenancePersonnel) {
        if (entity == null) return null;

        ElevatorImportTemplateExcel dto = new ElevatorImportTemplateExcel();

        dto.setRescueCode(entity.getRescueCode());
        dto.setRegisterCode(entity.getRegisterCode());
        dto.setElevatorNo(entity.getElevatorNo());
        dto.setElevatorName(entity.getElevatorName());
        dto.setElevatorType(entity.getElevatorType());
        dto.setUsageStatus(ElevatorUsageStatusEnum.getByCode(entity.getUsageStatus()).getDescription());
        dto.setNextInspectionDate(formatDate(entity.getNextInspectionDate()));
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());
        dto.setOperationStartDate(formatDate(entity.getOperationStartDate()));
        dto.setMaintenanceType(entity.getMaintenanceType());
        dto.setPropertyOwner(entity.getPropertyOwner());
        dto.setFactorySerialNumber(entity.getFactorySerialNumber());
        dto.setInstallationCompany(entity.getInstallationCompany());
        dto.setRenovationDate(formatDate(entity.getRenovationDate()));
        dto.setDriveType(entity.getDriveType());
        dto.setInspectionAgency(entity.getInspectionAgency());
        dto.setRegistrationAuthority(entity.getRegistrationAuthority());
        dto.setRegistrationDate(formatDate(entity.getRegistrationDate()));
        dto.setAddress(entity.getLocation());
        dto.setProvince(entity.getProvince());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setProjectName(entity.getProjectName());
        dto.setUsingUnit(entity.getUsingUnit());

        if (propertyInfo != null) {
            dto.setUsingUnitManager(propertyInfo.getUsingUnitManager());
            dto.setUsingUnitManagerPhone(propertyInfo.getUsingUnitManagerPhone());
            dto.setUsingUnit(propertyInfo.getUsingUnit());
        }

        if (community != null) {
            dto.setRealEstateBrand(community.getRealEstateBrand());
            dto.setProjectType(ProjectTypeEnum.getByCode(community.getProjectType()).getDescription());
            dto.setSafetyOfficerName(community.getSafetyOfficerName());
            dto.setSafetyOfficerPhone(community.getSafetyOfficerPhone());
        }
        if (maintenanceUnit != null) {
            dto.setMaintenanceUnitName(maintenanceUnit.getMaintainerUnitName());
            dto.setMaintenanceUnitManager(maintenanceUnit.getMaintainerUnitManager());
            dto.setMaintenanceUnitManagerPhone(maintenanceUnit.getMaintainerUnitManagerPhone());
        }
        if (maintenanceTeam != null) {
            dto.setTeamName(maintenanceTeam.getTeamName());
            dto.setTeamLeaderName(maintenanceTeam.getLeaderName());
            dto.setTeamLeaderPhone(maintenanceTeam.getLeaderPhone());
        }
        if (maintenancePersonnel != null) {
            dto.setWorkerName(maintenancePersonnel.getName());
            dto.setWorkerPhone(maintenancePersonnel.getPhone());
        }

        return dto;
    }

    // ---- 工具方法 ----
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // 可选：记录日志或抛出业务异常
            return null;
        }
    }

    private static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

}
