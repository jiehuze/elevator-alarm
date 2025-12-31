package com.schedule.elevator.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MaintenanceQueryDTO implements Serializable {
    private Integer level;
    private Long maintenanceUnitId; // 维保单位ID
    private Long maintenanceTeamId; // 维保组ID（可关联 future maintenance_teams 表）
    private Long maintenancePersonnelId; // 维保人员ID
    private String maintainerUnitName; // 维护单位名称
    private String district; // 区
}
