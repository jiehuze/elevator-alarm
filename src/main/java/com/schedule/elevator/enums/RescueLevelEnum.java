package com.schedule.elevator.enums;

/**
 * 救援级别枚举
 */
public enum RescueLevelEnum {
    
    /**
     * 一级救援
     */
    LEVEL_1(1, "一级救援"),
    
    /**
     * 二级救援
     */
    LEVEL_2(2, "二级救援"),
    
    /**
     * 三级救援
     */
    LEVEL_3(3, "三级救援");

    private final Integer code;
    private final String description;

    RescueLevelEnum(Integer code, String description) {
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
    public static RescueLevelEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RescueLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }

    /**
     * 根据状态描述获取枚举
     */
    public static RescueLevelEnum getByDescription(String description) {
        if (description == null) {
            return null;
        }
        for (RescueLevelEnum level : values()) {
            if (level.getDescription().equals(description)) {
                return level;
            }
        }
        return null;
    }
}
