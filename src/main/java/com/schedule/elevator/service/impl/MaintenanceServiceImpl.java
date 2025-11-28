package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dto.NearbyMaintenanceDTO;
import com.schedule.elevator.entity.Maintenance;
import com.schedule.elevator.dao.mapper.MaintenanceMapper;
import com.schedule.elevator.service.IMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Service
public class MaintenanceServiceImpl extends ServiceImpl<MaintenanceMapper, Maintenance>
        implements IMaintenanceService {
    @Autowired
    private MaintenanceMapper maintenanceMapper;

    @Override
    public List<NearbyMaintenanceDTO> getNearby(BigDecimal centerLat, BigDecimal centerLng, BigDecimal radiusKm) {
        // 1. 校验输入
        if (centerLat == null || centerLng == null || radiusKm == null) {
            throw new IllegalArgumentException("中心点或半径不能为空");
        }
        if (radiusKm.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }
        // 可选：限制最大半径（防恶意请求）
        if (radiusKm.compareTo(new BigDecimal("300")) > 0) {
            radiusKm = new BigDecimal("300");
        }

        // 2. 计算纬度偏移（1° ≈ 111 km）
        BigDecimal latDelta = radiusKm.divide(new BigDecimal("111"), 8, RoundingMode.HALF_UP);

        // 3. 计算经度偏移（需除以 cos(lat)）
        double latRadians = Math.toRadians(centerLat.doubleValue());
        double cosLat = Math.cos(latRadians);
        // 防止 cosLat 接近 0（极地），但中国地区安全
        if (cosLat < 0.01) cosLat = 0.01;

        BigDecimal lngDelta = radiusKm
                .divide(new BigDecimal("111"), 8, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(cosLat), 8, RoundingMode.HALF_UP);

        // 4. 计算边界
        BigDecimal latMin = centerLat.subtract(latDelta);
        BigDecimal latMax = centerLat.add(latDelta);
        BigDecimal lngMin = centerLng.subtract(lngDelta);
        BigDecimal lngMax = centerLng.add(lngDelta);

        // 5. 调用 Mapper
        List<NearbyMaintenanceDTO> list = maintenanceMapper.selectNearby(
                centerLat, centerLng, radiusKm,
                latMin, latMax,
                lngMin, lngMax
        );

        // 6. 可选：统一距离精度
        for (NearbyMaintenanceDTO dto : list) {
            if (dto.getDistanceKm() != null) {
                dto.setDistanceKm(dto.getDistanceKm().setScale(2, RoundingMode.HALF_UP));
            }
        }

        return list;
    }
}