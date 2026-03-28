package com.schedule.elevator.enums;

/**
 * 电梯类型枚举
 */
public enum ElevatorTypeEnum {
    
    /**
     * 曳引驱动乘客电梯
     */
    TRACTION_PASSENGER("曳引驱动乘客电梯", "traction_passenger"),
    
    /**
     * 曳引驱动载货电梯
     */
    TRACTION_CARGO("曳引驱动载货电梯", "traction_cargo"),
    
    /**
     * 曳引驱动观光电梯
     */
    TRACTION_OBSERVATION("曳引驱动观光电梯", "traction_observation"),
    
    /**
     * 强制驱动载货电梯
     */
    FORCED_DRIVE_CARGO("强制驱动载货电梯", "forced_drive_cargo"),
    
    /**
     * 液压乘客电梯
     */
    HYDRAULIC_PASSENGER("液压乘客电梯", "hydraulic_passenger"),
    
    /**
     * 液压载货电梯
     */
    HYDRAULIC_CARGO("液压载货电梯", "hydraulic_cargo"),
    
    /**
     * 自动扶梯
     */
    ESCALATOR("自动扶梯", "escalator"),
    
    /**
     * 自动人行道
     */
    MOVING_WALKWAY("自动人行道", "moving_walkway"),
    
    /**
     * 防爆电梯
     */
    EXPLOSION_PROOF("防爆电梯", "explosion_proof"),
    
    /**
     * 消防员电梯
     */
    FIREFIGHTER_ELEVATOR("消防员电梯", "firefighter_elevator"),
    
    /**
     * 杂物电梯
     */
    SERVICE_ELEVATOR("杂物电梯", "service_elevator");

    private final String description;
    private final String code;

    ElevatorTypeEnum(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    /**
     * 根据描述获取枚举
     */
    public static ElevatorTypeEnum getByDescription(String description) {
        for (ElevatorTypeEnum type : values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据代码获取枚举
     */
    public static ElevatorTypeEnum getByCode(String code) {
        for (ElevatorTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
