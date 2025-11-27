package com.schedule.elevator.dto;

import lombok.Getter;

@Getter
public enum RescueLevelEnum {
    GENERAL(1, "一般"),
    URGENT(2, "紧急"),
    CRITICAL(3, "特急");

    private final int code;
    private final String desc;

    RescueLevelEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) return "未知";
        for (RescueLevelEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return "未知";
    }
}
