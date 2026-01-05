package com.schedule.elevator.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.schedule.elevator.entity.FaultRecord;
import com.schedule.elevator.entity.WorkOrderProgress;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HandleDTO extends WorkOrderProgress implements Serializable {
    private Integer orderType;
    private Integer injuredCount; // 受伤人数
    private Integer trappedCount; // 被困人数
    private Integer suspectedDeathCount; // 疑似死亡人数
    private List<FaultRecord> faults;
}
