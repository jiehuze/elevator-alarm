package com.schedule.excel;

import com.schedule.elevator.dto.MaintenanceDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.entity.MaintenanceTeam;
import com.schedule.elevator.entity.MaintenanceUnit;
import com.schedule.elevator.entity.PropertyInfo;

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
        entity.setUsageStatus(dto.getUsageStatus());
        entity.setNextInspectionDate(parseDate(dto.getNextInspectionDate()));
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setOperationStartDate(parseDate(dto.getOperationStartDate()));
        entity.setMaintenanceType(dto.getMaintenanceType());
        entity.setPropertyOwner(dto.getPropertyOwner());
        entity.setFactorySerialNumber(dto.getFactorySerialNumber());
        entity.setInstallationCompany(dto.getInstallationCompany());
        entity.setRenovationDate(parseDate(dto.getRenovationDate()));
        entity.setDriveType(dto.getDriveType());
        entity.setInspectionAgency(dto.getInspectionAgency());
        entity.setRegistrationAuthority(dto.getRegistrationAuthority());
        entity.setRegistrationDate(parseDate(dto.getRegistrationDate()));
        entity.setLocation(dto.getLocation());
        entity.setProvince(dto.getProvince());
        entity.setCity(dto.getCity());
        entity.setDistrict(dto.getDistrict());
        entity.setProjectName(dto.getProjectName());
        entity.setRealEstateBrand(dto.getRealEstateBrand());
        entity.setProjectType(dto.getProjectType());
        entity.setUserUnit(dto.getUserUnit());

        return entity;
    }

    public static MaintenanceDTO toMaintenanceEntity(ElevatorImportTemplateExcel dto) {
        if (dto == null) return null;

        MaintenanceDTO entity = new MaintenanceDTO();
        entity.setMaintenanceUnit(new MaintenanceUnit());
        entity.setMaintenanceTeam(new MaintenanceTeam());

        entity.getMaintenanceUnit().setMaintainerUnitName(dto.getMaintenanceUnitName());
        entity.getMaintenanceUnit().setMaintainerUnitManager(dto.getMaintenanceUnitManager());
        entity.getMaintenanceUnit().setMaintainerUnitManagerPhone(dto.getMaintenanceUnitManagerPhone());

        entity.getMaintenanceTeam().setTeamName(dto.getTeamName());
        entity.getMaintenanceTeam().setLeaderName(dto.getTeamLeaderName());
        entity.getMaintenanceTeam().setLeaderPhone(dto.getTeamLeaderPhone());
        entity.getMaintenanceTeam().setWorkerName(dto.getWorkerName());
        entity.getMaintenanceTeam().setWorkerPhone(dto.getWorkerPhone());

        return entity;
    }

    public static PropertyInfo toPropertyEntity(ElevatorImportTemplateExcel dto) {
        if (dto == null) return null;

        PropertyInfo entity = new PropertyInfo();

        entity.setUserUnit(dto.getUserUnit());
        entity.setUserUnitManager(dto.getUserUnitManager());
        entity.setUserUnitManagerPhone(dto.getUserUnitManagerPhone());
        entity.setSafetyOfficerName(dto.getSafetyOfficerName());
        entity.setSafetyOfficerPhone(dto.getSafetyOfficerPhone());

        return entity;
    }

    /**
     * 将 Entity 转换为 DTO（用于导出或返回前端）
     */
    public static ElevatorImportTemplateExcel toDTO(ElevatorInfo entity) {
        if (entity == null) return null;

        ElevatorImportTemplateExcel dto = new ElevatorImportTemplateExcel();

        dto.setRescueCode(entity.getRescueCode());
        dto.setRegisterCode(entity.getRegisterCode());
        dto.setElevatorNo(entity.getElevatorNo());
        dto.setElevatorName(entity.getElevatorName());
        dto.setElevatorType(entity.getElevatorType());
        dto.setUsageStatus(entity.getUsageStatus());
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
        dto.setLocation(entity.getLocation());
        dto.setProvince(entity.getProvince());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setProjectName(entity.getProjectName());
        dto.setRealEstateBrand(entity.getRealEstateBrand());
        dto.setProjectType(entity.getProjectType());
        dto.setUserUnit(entity.getUserUnit());

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
