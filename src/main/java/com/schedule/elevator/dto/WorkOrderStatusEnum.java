package com.schedule.elevator.dto;

import lombok.Getter;

@Getter
public enum WorkOrderStatusEnum {
    // —————— 流程进行中 ——————
    SELECT_RESCUE(1, "确定救援"),
    ASSIGNED(2, "已派单"),
    EN_ROUTE(3, "赶往现场"),
    ARRIVED(4, "到达现场"),
    HANDLING(5, "故障处理"),
    RECOVERED(6, "故障恢复"),

    // —————— 最终状态 ——————
    COMPLETED(100, "工单完成"),   // 整个流程走完
    CANCELLED(99, "工单取消");   // 中途取消

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
