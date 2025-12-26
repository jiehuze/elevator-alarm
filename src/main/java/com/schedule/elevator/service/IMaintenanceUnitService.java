package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.NearbyMaintenanceUnitDTO;
import com.schedule.elevator.entity.MaintenanceUnit;

import java.math.BigDecimal;
import java.util.List;

public interface IMaintenanceUnitService extends IService<MaintenanceUnit> {
    List<NearbyMaintenanceUnitDTO> getNearby(BigDecimal centerLat, BigDecimal centerLng, BigDecimal radiusKm);

    Page<MaintenanceUnit> page(MaintenanceUnit mt, int current, int size);

    long getOrCreateMaintenanceUnitId(MaintenanceUnit entity) throws Exception;
}