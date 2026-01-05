package com.schedule.elevator.enums;

/**
 * 工单状态枚举
 */
public enum WorkOrderStatusEnum {

    /**
     * 创建工单
     */
    CREATED(0, "创建工单"),

    /**
     * 派单
     */
    DISPATCHED(1, "派单"),

    /**
     * 救援人员响应成功
     */
    RESCUE_RESPONSE_SUCCESS(2, "救援人员响应成功"),

    /**
     * 回拨安抚
     */
    CALLBACK_COMFORT(3, "回拨安抚"),

    /**
     * 救援人员到达现场
     */
    RESCUE_ARRIVED(4, "救援人员到达现场"),

    /**
     * 救援人员救援完成
     */
    RESCUE_COMPLETED(5, "救援人员救援完成"),

    /**
     * 救援回访
     */
    RESCUE_FOLLOW_UP(6, "救援回访"),

    /**
     * 维修回访
     */
    MAINTENANCE_FOLLOW_UP(7, "维修回访"),

    /**
     * 维修完成
     */
    MAINTENANCE_COMPLETED(8, "维修完成"),

    /**
     * 结案
     */
    CLOSED(99, "结案");

    private final Integer code;
    private final String description;

    WorkOrderStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取枚举
     */
    public static WorkOrderStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WorkOrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据状态描述获取枚举
     */
    public static WorkOrderStatusEnum getByDescription(String description) {
        if (description == null) {
            return null;
        }
        for (WorkOrderStatusEnum status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        return null;
    }
}
