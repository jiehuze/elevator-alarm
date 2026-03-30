package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(25)
@ContentRowHeight(20)
public class ElevatorImportTemplateExcel {

    // ========== 来自模板：电梯信息 ==========
    @ExcelProperty(value = "电梯注册代码\n电梯注册代码小于20位请上传登记证编号", index = 0)
    @ColumnWidth(25)
    private String registerCode; // 对应数据库 register_code（如果你已添加）

    @ExcelProperty(value = "电梯使用登记证编号", index = 1)
    @ColumnWidth(25)
    private String elevatorNo; // 可存入 description 或扩展字段（表中暂无直接对应）

    @ExcelProperty(value = "电梯名称", index = 2)
    @ColumnWidth(20)
    private String elevatorName; // elevator_name

    @ExcelProperty(value = "电梯类型", index = 3)
    @ColumnWidth(15)
    private String elevatorType; // elevator_type

    @ExcelProperty(value = "使用状态", index = 4)
    @ColumnWidth(12)
    private String usageStatus; // usage_status

    @ExcelProperty(value = "下次检验日期\n（格式yyyy-mm-dd）", index = 5)
    @ColumnWidth(18)
    private String nextInspectionDate; // next_inspection_date

    @ExcelProperty(value = "电梯品牌", index = 6)
    @ColumnWidth(15)
    private String brand; // brand

    @ExcelProperty(value = "电梯型号", index = 7)
    @ColumnWidth(15)
    private String model; // model

    @ExcelProperty(value = "开始运行时间\n（格式yyyy-mm-dd）", index = 8)
    @ColumnWidth(18)
    private String operationStartDate; // operation_start_date

    @ExcelProperty(value = "维保类型", index = 9)
    @ColumnWidth(15)
    private String maintenanceType; // maintenance_type

    @ExcelProperty(value = "电梯产权单位", index = 10)
    @ColumnWidth(25)
    private String propertyOwner; // property_owner

    @ExcelProperty(value = "出厂编号", index = 11)
    @ColumnWidth(20)
    private String factorySerialNumber; // factory_serial_number

    @ExcelProperty(value = "电梯安装单位", index = 12)
    @ColumnWidth(25)
    private String installationCompany; // installation_company

    @ExcelProperty(value = "电梯大修/改造日期\n（格式yyyy-mm-dd）", index = 13)
    @ColumnWidth(18)
    private String renovationDate; // renovation_date

    @ExcelProperty(value = "拖动方式", index = 14)
    @ColumnWidth(15)
    private String driveType; // drive_type

    @ExcelProperty(value = "电梯检验机构", index = 15)
    @ColumnWidth(25)
    private String inspectionAgency; // inspection_agency

    @ExcelProperty(value = "使用登记机构", index = 16)
    @ColumnWidth(25)
    private String registrationAuthority; // registration_authority

    @ExcelProperty(value = "使用登记日期\n（格式yyyy-mm-dd）", index = 17)
    @ColumnWidth(18)
    private String registrationDate; // registration_date

    // ========== 小区/项目信息 ==========
    @ExcelProperty(value = "省", index = 18)
    @ColumnWidth(10)
    private String province; // province

    @ExcelProperty(value = "市", index = 19)
    @ColumnWidth(10)
    private String city; // city

    @ExcelProperty(value = "区", index = 20)
    @ColumnWidth(10)
    private String district; // district

    @ExcelProperty(value = "电梯地址", index = 21)
    @ColumnWidth(50)
    private String address; // adress（对应数据库 location）

    @ExcelProperty(value = "项目名", index = 22)
    @ColumnWidth(25)
    private String projectName; // project_name

    @ExcelProperty(value = "小区所属地产品牌", index = 23)
    @ColumnWidth(20)
    private String realEstateBrand; // real_estate_brand

    @ExcelProperty(value = "项目类型", index = 24)
    @ColumnWidth(15)
    private String projectType; // project_type

    // ========== 使用单位信息 ==========
    @ExcelProperty(value = "使用单位名称", index = 25)
    @ColumnWidth(25)
    private String usingUnit; // user_unit

    @ExcelProperty(value = "使用单位负责人姓名", index = 26)
    @ColumnWidth(15)
    private String usingUnitManager; // user_unit_manager

    @ExcelProperty(value = "使用单位负责人手机号", index = 27)
    @ColumnWidth(15)
    private String usingUnitManagerPhone; // user_unit_manager_phone

    // ========== 安全员信息 ==========
    @ExcelProperty(value = "安全员姓名", index = 28)
    @ColumnWidth(15)
    private String safetyOfficerName; // 可存入 backup_contact

    @ExcelProperty(value = "安全员手机号", index = 29)
    @ColumnWidth(15)
    private String safetyOfficerPhone; // 可存入 backup_phone

    // ========== 维保单位及班组信息（可选存入 description 或扩展表）==========
    @ExcelProperty(value = "维保单位名称", index = 30)
    @ColumnWidth(25)
    private String maintenanceUnit;

    @ExcelProperty(value = "维保单位负责人姓名", index = 31)
    @ColumnWidth(15)
    private String maintenanceUnitManager;

    @ExcelProperty(value = "负责人手机号", index = 32)
    @ColumnWidth(15)
    private String maintenanceUnitManagerPhone;

    @ExcelProperty(value = "电梯所在班组名称", index = 33)
    @ColumnWidth(20)
    private String teamName;

    @ExcelProperty(value = "组长姓名", index = 34)
    @ColumnWidth(15)
    private String teamLeaderName;

    @ExcelProperty(value = "组长手机号", index = 35)
    @ColumnWidth(15)
    private String teamLeaderPhone;

    @ExcelProperty(value = "责任维保工人姓名", index = 36)
    @ColumnWidth(15)
    private String workerName;

    @ExcelProperty(value = "责任维保工人手机号", index = 37)
    @ColumnWidth(15)
    private String workerPhone;

    // ========== 其他关键字段 ==========
    @ExcelProperty(value = "救援识别码", index = 38)
    @ColumnWidth(12)
    private String rescueCode; // rescue_code

    // ⚠️ 注意：模板中没有直接叫“电梯编号”的列，
    // 但通常“电梯注册代码”或“电梯名称”可作为 elevator_no
    // 如果业务上“电梯编号” = “电梯注册代码”，则 registerCode → elevatorNo
}