package com.schedule.excel;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.schedule.elevator.dto.MaintenanceDTO;
import com.schedule.elevator.entity.*;
import com.schedule.elevator.enums.DistrictEnum;
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

        if (dto.getRescueCode() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，救援码不能为空");
        }
        entity.setRescueCode(dto.getRescueCode());

        if (dto.getRegisterCode() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRescueCode() + "，注册码不能为空");
        }
        entity.setRegisterCode(dto.getRegisterCode());

        if (dto.getElevatorNo() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，电梯编号不能为空");
        }
        entity.setElevatorNo(dto.getElevatorNo());

        if (dto.getElevatorName() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，电梯名称不能为空");
        }
        entity.setElevatorName(dto.getElevatorName());

        if (dto.getElevatorType() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，电梯类型不能为空");
        }
        entity.setElevatorType(dto.getElevatorType());

        if (dto.getUsageStatus() == null || dto.getUsageStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，使用状态不能为空");
        }
        ElevatorUsageStatusEnum usageStatus = ElevatorUsageStatusEnum.getByDescription(dto.getUsageStatus().trim());
        if (usageStatus == null) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，无效的使用状态：" + dto.getUsageStatus() + "，有效值为：在用、停用、注销");
        }
        entity.setUsageStatus(usageStatus.getCode());

        LocalDate nextInspectionDate = parseDate(dto.getNextInspectionDate());
        if (dto.getNextInspectionDate() != null && nextInspectionDate == null) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，下次检验时间为空或格式错误，请检查");
        }
        entity.setNextInspectionDate(nextInspectionDate);

        if (dto.getBrand() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，品牌不能为空");
        }
        entity.setBrand(dto.getBrand());

        if (dto.getModel() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，型号不能为空");
        }
        entity.setModel(dto.getModel());

        LocalDate operationStartDate = parseDate(dto.getOperationStartDate());
        if (dto.getOperationStartDate() != null && operationStartDate == null) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，开始运行时间为空或格式错误，请检查");
        }
        entity.setOperationStartDate(operationStartDate);

        if (dto.getMaintenanceType() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，维保类型不能为空");
        }
        entity.setMaintenanceType(dto.getMaintenanceType());

        if (dto.getPropertyOwner() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，产权单位不能为空");
        }
        entity.setMaintenancePersonnelName(dto.getWorkerName());

        if (dto.getFactorySerialNumber() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，出厂编号不能为空");
        }
        entity.setPropertyOwner(dto.getPropertyOwner());

//        if (dto.getInstallationCompany() == null){
//            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，电梯安装单位不能为空");
//        }
        entity.setFactorySerialNumber(dto.getFactorySerialNumber());

//        if (dto.getInstallationCompany() == null){
//            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，电梯安装单位不能为空");
//        }
        entity.setInstallationCompany(dto.getInstallationCompany());

        LocalDate renovationDate = parseDate(dto.getRenovationDate());
        if (dto.getRenovationDate() != null && renovationDate == null) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，大修/改造日期为空或格式错误，请检查");
        }
        entity.setRenovationDate(renovationDate);

        if (dto.getDriveType() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，驱动方式不能为空");
        }
        entity.setDriveType(dto.getDriveType());

        if (dto.getInspectionAgency() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，电梯检验机构不能为空");
        }
        entity.setInspectionAgency(dto.getInspectionAgency());

        if (dto.getRegistrationAuthority() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，使用登记机构不能为空");
        }
        entity.setRegistrationAuthority(dto.getRegistrationAuthority());

        LocalDate registrationDate = parseDate(dto.getRegistrationDate());
        if (dto.getRegistrationDate() != null && registrationDate == null) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，使用登记日期为空或格式错误，请检查");
        }
        entity.setRegistrationDate(registrationDate);

        if (dto.getAddress() == null){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，地址不能为空");
        }
        entity.setLocation(dto.getAddress());

        if (dto.getProvince() == null || !"河北省".equals(dto.getProvince())){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，省不能为空");
        }
        entity.setProvince(dto.getProvince());

        if (dto.getCity() == null || !"承德市".equals(dto.getCity())){
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，市不能为空");
        }
        entity.setCity(dto.getCity());

        // 校验区县名称是否合法
        if (StringUtils.isNotBlank(dto.getDistrict())) {
            DistrictEnum districtEnum = DistrictEnum.getByName(dto.getDistrict().trim());
            if (districtEnum == null) {
                throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，无效的区县名称：" + dto.getDistrict() +
                        "，有效值为：双桥区、双滦区、鹰手营子矿区、承德县、兴隆县、平泉市、滦平县、隆化县、丰宁满族自治县、宽城满族自治县、围场满族蒙古族自治县、高新区");
            }
            entity.setDistrict(districtEnum.getName());
        }
        if (dto.getProjectName() == null) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，项目名不能为空");
        }
        entity.setProjectName(dto.getProjectName());

        if (dto.getUsingUnit() == null) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，使用单位名称不能为空");
        }
        entity.setUsingUnit(dto.getUsingUnit());

        if (dto.getMaintenanceUnit() == null) {
            throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，维保单位名称不能为空");
        }
        entity.setMaintenanceUnit(dto.getMaintenanceUnit());

        if (StringUtils.isNotBlank(dto.getProjectType())) {
            ProjectTypeEnum projectType = ProjectTypeEnum.getByDescription(dto.getProjectType().trim());
            if (projectType == null) {
                throw new IllegalArgumentException("电梯：" + dto.getRegisterCode() + "，无效的项目类型：" + dto.getProjectType() +
                        "，有效值为：住宅区、办公楼、商业区、宾馆饭店、医院、学校、交通场所、文体娱场馆、其他");
            }
            entity.setProjectType(projectType.getCode());
        }

        return entity;
    }

    public static Community toCommunityEntity(ElevatorImportTemplateExcel dto) {
        if (dto == null) {
            throw new IllegalArgumentException("导入数据不能为空");
        }
        Community community = new Community();

        community.setProjectName(dto.getProjectName())
                .setDistrict(dto.getDistrict())
                .setCity(dto.getCity())
                .setRealEstateBrand(dto.getRealEstateBrand())
                .setProvince(dto.getProvince())
                .setAddress(dto.getAddress())
                .setUsingUnit(dto.getUsingUnit());

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

        entity.getMaintenanceUnit().setMaintenanceUnit(dto.getMaintenanceUnit());
        entity.getMaintenanceUnit().setMaintenanceUnitManager(dto.getMaintenanceUnitManager());
        entity.getMaintenanceUnit().setMaintenanceUnitManagerPhone(dto.getMaintenanceUnitManagerPhone());

        entity.getMaintenanceTeam().setTeamName(dto.getTeamName());
        entity.getMaintenanceTeam().setLeaderName(dto.getTeamLeaderName());
        entity.getMaintenanceTeam().setLeaderPhone(dto.getTeamLeaderPhone());
        entity.getMaintenanceTeam().setProvince(dto.getProvince());
        entity.getMaintenanceTeam().setCity(dto.getCity());
        entity.getMaintenanceTeam().setDistrict(dto.getDistrict());

        entity.getMaintenancePersonnel().setName(dto.getWorkerName());
        entity.getMaintenancePersonnel().setPhone(dto.getWorkerPhone());
        entity.getMaintenancePersonnel().setCompany(dto.getMaintenanceUnit());
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

    public static SafetyOfficer toSafetyOfficerEntity(ElevatorImportTemplateExcel dto) {
        if (dto == null) return null;

        SafetyOfficer entity = new SafetyOfficer();

        entity.setSafetyOfficerName(dto.getSafetyOfficerName());
        entity.setSafetyOfficerPhone(dto.getSafetyOfficerPhone());
        entity.setUsingUnit(dto.getUsingUnit());
        entity.setStatus(1);

        return entity;
    }

    /**
     * 将 Entity 转换为 DTO（用于导出或返回前端）
     */
    public static ElevatorImportTemplateExcel toDTO(ElevatorInfo entity,
                                                    Community community,
                                                    SafetyOfficer safetyOfficer,
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
            if (ProjectTypeEnum.getByCode(community.getProjectType()) != null) {
                dto.setProjectType(ProjectTypeEnum.getByCode(community.getProjectType()).getDescription());
            }
        }

        if (safetyOfficer != null) {
            dto.setSafetyOfficerName(safetyOfficer.getSafetyOfficerName());
            dto.setSafetyOfficerPhone(safetyOfficer.getSafetyOfficerPhone());
        }

        if (maintenanceUnit != null) {
            dto.setMaintenanceUnit(maintenanceUnit.getMaintenanceUnit());
            dto.setMaintenanceUnitManager(maintenanceUnit.getMaintenanceUnitManager());
            dto.setMaintenanceUnitManagerPhone(maintenanceUnit.getMaintenanceUnitManagerPhone());
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
