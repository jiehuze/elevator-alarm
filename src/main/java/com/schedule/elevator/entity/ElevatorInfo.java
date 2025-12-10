package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 电梯信息实体类
 * 对应数据库表：elevator_info
 */
@Data
@TableName("elevator_info")
public class ElevatorInfo {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 救援识别码（如96365平台6位编码）
     */
    @TableField("rescue_code")
    private String rescueCode;

    /**
     * 电梯注册码（特种设备20位注册码）
     */
    @TableField("register_code")
    private String registerCode;

    /**
     * 电梯编号（唯一）
     */
    @TableField("elevator_no")
    private String elevatorNo;

    /**
     * 电梯名称
     */
    @TableField("elevator_name")
    private String elevatorName;

    /**
     * 电梯类型
     */
    @TableField("elevator_type")
    private String elevatorType;

    /**
     * 使用状态：in_use-在用, stopped-停用, cancelled-注销
     */
    @TableField("usage_status")
    private String usageStatus;

    /**
     * 下次检验时间
     */
    @TableField("next_inspection_date")
    private LocalDate nextInspectionDate;

    /**
     * 电梯品牌
     */
    @TableField("brand")
    private String brand;

    /**
     * 电梯型号
     */
    @TableField("model")
    private String model;

    /**
     * 开始运行时间
     */
    @TableField("operation_start_date")
    private LocalDate operationStartDate;

    /**
     * 维保类型
     */
    @TableField("maintenance_type")
    private String maintenanceType;

    @TableField("maintenance_unit_id")
    private Long maintenanceUnitId; // 维保单位ID

    @TableField("maintenance_team_id")
    private Long maintenanceTeamId; // 维保班组ID

    /**
     * 电梯产权单位
     */
    @TableField("property_owner")
    private String propertyOwner;

    /**
     * 出厂编号
     */
    @TableField("factory_serial_number")
    private String factorySerialNumber;

    /**
     * 电梯安装单位
     */
    @TableField("installation_company")
    private String installationCompany;

    /**
     * 电梯大修/改造日期
     */
    @TableField("renovation_date")
    private LocalDate renovationDate;

    /**
     * 拖动方式
     */
    @TableField("drive_type")
    private String driveType;

    /**
     * 电梯检验机构
     */
    @TableField("inspection_agency")
    private String inspectionAgency;

    /**
     * 使用登记机构
     */
    @TableField("registration_authority")
    private String registrationAuthority;

    /**
     * 使用登记日期
     */
    @TableField("registration_date")
    private LocalDate registrationDate;

    /**
     * 电梯位置（如楼栋、楼层）
     */
    @TableField("location")
    private String location;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区/县
     */
    @TableField("district")
    private String district;

    /**
     * 项目名
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 小区所属地产品牌
     */
    @TableField("real_estate_brand")
    private String realEstateBrand;

    /**
     * 项目类型
     */
    @TableField("project_type")
    private String projectType;

    /**
     * 使用单位名称
     */
    @TableField("using_unit")
    private String usingUnit;

    /**
     * 使用单位ID
     */
    @TableField("using_unit_id")
    private Long usingUnitId;

    /**
     * 记录创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 记录最后更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}