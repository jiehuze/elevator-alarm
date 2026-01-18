package com.schedule.elevator.enums;

import lombok.Getter;

/**
 * 导出类型枚举
 */
@Getter
public enum ExportTypeEnum {

    MONTHLY_REPORT(1, "月报"),
    SEMI_ANNUAL_REPORT(2, "半年报"),
    ANNUAL_REPORT(3, "年报"),
    WORK_ORDER_REPORT(4, "工单报告"),
    WORK_ORDER_LIST(5, "工单列表"),
    ELEVATOR_INFO_LIST(6, "电梯信息list"),
    MAINTENANCE_UNIT_LIST(7, "维保单位list"),
    MAINTENANCE_PERSONNEL_LIST(8, "维修人员list");

    private final Integer code;
    private final String description;

    ExportTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据编码获取枚举
     */
    public static ExportTypeEnum getByCode(Integer code) {
        for (ExportTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为报告类型
     */
    public boolean isReport() {
        return this == MONTHLY_REPORT || this == SEMI_ANNUAL_REPORT || this == ANNUAL_REPORT || this == WORK_ORDER_REPORT;
    }
}
