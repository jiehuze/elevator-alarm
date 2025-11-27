package com.schedule.elevator.dto;

import lombok.Getter;
@Getter
public enum WorkOrderStatusEnum {
    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    CLOSED(2, "已关闭");

    private final int code;
    private final String desc;

    WorkOrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) return "未知";
        for (WorkOrderStatusEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return "未知";
    }
}
