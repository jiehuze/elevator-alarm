// elevator-alarm/src/main/java/com/schedule/elevator/dto/NearbyMaintenanceDTO.java
package com.schedule.elevator.dto;

import com.schedule.elevator.entity.MaintenanceUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class NearbyMaintenanceUnitDTO extends MaintenanceUnit {
    /**
     * 计算出的距离（单位：公里）
     */
    private BigDecimal distanceKm;
}