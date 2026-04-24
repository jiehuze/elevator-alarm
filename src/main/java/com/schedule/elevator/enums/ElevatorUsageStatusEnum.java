package com.schedule.elevator.enums;

/**
 * 电梯使用状态枚举
 */
public enum ElevatorUsageStatusEnum {

    /**
     * 在用
     */
    IN_USE(1, "在用"),

    /**
     * 停用
     */
    STOPPED(2, "停用"),

    /**
     * 注销
     */
    CANCELLED(3, "注销"),

    /**
     * 脱保
     */
    OUT_OF_SERVICE(4, "脱保");

    private final Integer code;
    private final String description;

    ElevatorUsageStatusEnum(Integer code, String description) {
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
    public static ElevatorUsageStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ElevatorUsageStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据状态描述获取枚举
     */
    public static ElevatorUsageStatusEnum getByDescription(String description) {
        if (description == null) {
            return null;
        }
        for (ElevatorUsageStatusEnum status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        return null;
    }
}
