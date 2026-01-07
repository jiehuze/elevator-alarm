package com.schedule.elevator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单类型枚举
 */
@Getter
@AllArgsConstructor
public enum WorkOrderTypeEnum {

    TRAPPED_PEOPLE(1, "困人工单"),
    FAULT(2, "故障工单"),
    COMPLAINT(3, "投诉"),
    CONSULTATION(4, "咨询"),
    SELF_ESCAPE(5, "自行脱困"),
    FALSE_ALARM(6, "误报");

    private final Integer code;
    private final String description;

    /**
     * 根据编码获取枚举
     */
    public static WorkOrderTypeEnum getByCode(Integer code) {
        for (WorkOrderTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
