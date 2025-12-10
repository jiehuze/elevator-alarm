package com.schedule.elevator.dto;

import com.schedule.elevator.entity.MaintenanceTeam;
import com.schedule.elevator.entity.MaintenanceUnit;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class MaintenanceDTO implements Serializable {
    private MaintenanceUnit maintenanceUnit;
    private MaintenanceTeam maintenanceTeam;
}
