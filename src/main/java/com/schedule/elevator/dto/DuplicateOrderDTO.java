package com.schedule.elevator.dto;

import com.schedule.elevator.entity.WorkOrder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DuplicateOrderDTO {
    private String district;
    private String rescueCode; // 救援识别码
    private String registerCode; // 电梯注册码
    private Long count;
    private List<WorkOrder> list;
}
