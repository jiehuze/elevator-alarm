package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.NearbyMaintenanceDTO;
import com.schedule.elevator.entity.Maintenance;

import java.math.BigDecimal;
import java.util.List;

public interface IMaintenanceService extends IService<Maintenance> {
    List<NearbyMaintenanceDTO> getNearby(BigDecimal centerLat, BigDecimal centerLng, BigDecimal radiusKm);
}