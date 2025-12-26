package com.schedule.elevator.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TeamPersonDTO implements Serializable {
    private Long maintenanceTeamId;
    private List<Long> addMaintenancePersonnelIds;
    private List<Long> deleteMaintenancePersonnelIds;
}
