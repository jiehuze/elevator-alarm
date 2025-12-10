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
    @ExcelProperty("电梯注册代码\n电梯注册代码小于20位请上传登记证编号")
    @ColumnWidth(25)
    private String registerCode; // 对应数据库 register_code（如果你已添加）

    @ExcelProperty("电梯使用登记证编号")
    @ColumnWidth(25)
    private String elevatorNo; // 可存入 description 或扩展字段（表中暂无直接对应）

    @ExcelProperty("电梯名称")
    @ColumnWidth(20)
    private String elevatorName; // elevator_name

    @ExcelProperty("电梯类型")
    @ColumnWidth(15)
    private String elevatorType; // elevator_type

    @ExcelProperty("使用状态")
    @ColumnWidth(12)
    private String usageStatus; // usage_status

    @ExcelProperty("下次检验日期\n（格式yyyy-mm-dd）")
    @ColumnWidth(18)
    private String nextInspectionDate; // next_inspection_date

    @ExcelProperty("电梯品牌")
    @ColumnWidth(15)
    private String brand; // brand

    @ExcelProperty("电梯型号")
    @ColumnWidth(15)
    private String model; // model

    @ExcelProperty("开始运行时间\n（格式yyyy-mm-dd）")
    @ColumnWidth(18)
    private String operationStartDate; // operation_start_date

    @ExcelProperty("维保类型")
    @ColumnWidth(15)
    private String maintenanceType; // maintenance_type

    @ExcelProperty("电梯产权单位")
    @ColumnWidth(25)
    private String propertyOwner; // property_owner

    @ExcelProperty("出厂编号")
    @ColumnWidth(20)
    private String factorySerialNumber; // factory_serial_number

    @ExcelProperty("电梯安装单位")
    @ColumnWidth(25)
    private String installationCompany; // installation_company

    @ExcelProperty("电梯大修/改造日期\n（格式yyyy-mm-dd）")
    @ColumnWidth(18)
    private String renovationDate; // renovation_date

    @ExcelProperty("拖动方式")
    @ColumnWidth(15)
    private String driveType; // drive_type

    @ExcelProperty("电梯检验机构")
    @ColumnWidth(25)
    private String inspectionAgency; // inspection_agency

    @ExcelProperty("使用登记机构")
    @ColumnWidth(25)
    private String registrationAuthority; // registration_authority

    @ExcelProperty("使用登记日期\n（格式yyyy-mm-dd）")
    @ColumnWidth(18)
    private String registrationDate; // registration_date

    // ========== 小区/项目信息 ==========
    @ExcelProperty("省")
    @ColumnWidth(10)
    private String province; // province

    @ExcelProperty("市")
    @ColumnWidth(10)
    private String city; // city

    @ExcelProperty("区")
    @ColumnWidth(10)
    private String district; // district

    @ExcelProperty("电梯地址")
    @ColumnWidth(50)
    private String location; // location（对应数据库 location）

    @ExcelProperty("项目名")
    @ColumnWidth(25)
    private String projectName; // project_name

    @ExcelProperty("小区所属地产品牌")
    @ColumnWidth(20)
    private String realEstateBrand; // real_estate_brand

    @ExcelProperty("项目类型")
    @ColumnWidth(15)
    private String projectType; // project_type

    // ========== 使用单位信息 ==========
    @ExcelProperty("使用单位名称")
    @ColumnWidth(25)
    private String usingUnit; // user_unit

    @ExcelProperty("使用单位负责人姓名")
    @ColumnWidth(15)
    private String usingUnitManager; // user_unit_manager

    @ExcelProperty("使用单位负责人手机号")
    @ColumnWidth(15)
    private String usingUnitManagerPhone; // user_unit_manager_phone

    // ========== 安全员信息 ==========
    @ExcelProperty("安全员姓名")
    @ColumnWidth(15)
    private String safetyOfficerName; // 可存入 backup_contact

    @ExcelProperty("安全员手机号")
    @ColumnWidth(15)
    private String safetyOfficerPhone; // 可存入 backup_phone

    // ========== 维保单位及班组信息（可选存入 description 或扩展表）==========
    @ExcelProperty("维保单位名称")
    @ColumnWidth(25)
    private String maintenanceUnitName;

    @ExcelProperty("维保单位负责人姓名")
    @ColumnWidth(15)
    private String maintenanceUnitManager;

    @ExcelProperty("负责人手机号")
    @ColumnWidth(15)
    private String maintenanceUnitManagerPhone;

    @ExcelProperty("电梯所在班组名称")
    @ColumnWidth(20)
    private String teamName;

    @ExcelProperty("组长姓名")
    @ColumnWidth(15)
    private String teamLeaderName;

    @ExcelProperty("组长手机号")
    @ColumnWidth(15)
    private String teamLeaderPhone;

    @ExcelProperty("责任维保工人姓名")
    @ColumnWidth(15)
    private String workerName;

    @ExcelProperty("责任维保工人手机号")
    @ColumnWidth(15)
    private String workerPhone;

    // ========== 其他关键字段 ==========
    @ExcelProperty("救援识别码")
    @ColumnWidth(12)
    private String rescueCode; // rescue_code

    // ⚠️ 注意：模板中没有直接叫“电梯编号”的列，
    // 但通常“电梯注册代码”或“电梯名称”可作为 elevator_no
    // 如果业务上“电梯编号” = “电梯注册代码”，则 registerCode → elevatorNo
}