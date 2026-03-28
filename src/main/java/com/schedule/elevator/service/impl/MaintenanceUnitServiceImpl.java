package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dto.MaintenanceQueryDTO;
import com.schedule.elevator.dto.NearbyMaintenanceDTO;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.MaintenanceUnit;
import com.schedule.elevator.dao.mapper.MaintenanceMapper;
import com.schedule.elevator.service.IMaintenanceUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Service
public class MaintenanceUnitServiceImpl extends ServiceImpl<MaintenanceMapper, MaintenanceUnit>
        implements IMaintenanceUnitService {
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

    @Override
    public Page<MaintenanceUnit> page(MaintenanceUnit mt, int current, int size) {
        Page<MaintenanceUnit> page = new Page<>(current, size);
        LambdaQueryWrapper<MaintenanceUnit> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(mt.getMaintenanceUnit()), MaintenanceUnit::getMaintenanceUnit, mt.getMaintenanceUnit());
        queryWrapper.eq(StringUtils.isNotBlank(mt.getMaintenanceUnitManager()), MaintenanceUnit::getMaintenanceUnitManager, mt.getMaintenanceUnitManager());
        queryWrapper.eq(StringUtils.isNotBlank(mt.getMaintenanceUnitManagerPhone()), MaintenanceUnit::getMaintenanceUnitManagerPhone, mt.getMaintenanceUnitManagerPhone());
        queryWrapper.eq(StringUtils.isNotBlank(mt.getMaintenanceUnitCode()), MaintenanceUnit::getMaintenanceUnitCode, mt.getMaintenanceUnitCode());
        queryWrapper.eq(StringUtils.isNotBlank(mt.getMaintenanceUnitStatus()), MaintenanceUnit::getMaintenanceUnitStatus, mt.getMaintenanceUnitStatus());

        return this.page(page, queryWrapper);
    }

    @Override
    public List<MaintenanceUnit> listByQuery(SearchDTO queryDTO) {
        LambdaQueryWrapper<MaintenanceUnit> eq = new LambdaQueryWrapper<MaintenanceUnit>()
                .eq(queryDTO.getMaintenanceUnitId() != null, MaintenanceUnit::getId, queryDTO.getMaintenanceUnitId())
                .like(StringUtils.isNotBlank(queryDTO.getMaintenanceUnit()), MaintenanceUnit::getMaintenanceUnit, queryDTO.getMaintenanceUnit())
                .ge(queryDTO.getLevel() != null, MaintenanceUnit::getLevel, queryDTO.getLevel());

        return this.list(eq);
    }

    @Override
    public long getOrCreateMaintenanceUnitId(MaintenanceUnit entity) throws Exception {
        // 1. 先查询是否已存在
        MaintenanceUnit existing = this.getOne(new LambdaQueryWrapper<MaintenanceUnit>()
                .eq(MaintenanceUnit::getMaintenanceUnit, entity.getMaintenanceUnit()));
//                .eq(MaintenanceUnit::getMaintenanceUnitManagerPhone, entity.getMaintenanceUnitManagerPhone()));

//        if (!entity.getMaintenanceUnitManagerPhone().equals(existing.getMaintenanceUnitManagerPhone())) {
//            throw new Exception("维保单位" + entity.getMaintenanceUnit() + "电话变更，请确认；");
//        }
//        if (!entity.getMaintenanceUnitManager().equals(existing.getMaintenanceUnitManager())) {
//            throw new Exception("维保单位" + entity.getMaintenanceUnit() + "负责人:" + entity.getMaintenanceUnitManager() + "与系统内不一致，请确认；");
//        }

        //维保单位负责人和电话可以直接更新
        if (existing != null) {
            this.update(entity, new LambdaQueryWrapper<MaintenanceUnit>().eq(MaintenanceUnit::getId, entity.getId()));
            return existing.getId();
        }

        boolean saved = this.save(entity);
        if (!saved) {
            throw new RuntimeException("维保单位" + entity.getMaintenanceUnit() + "插入失败");
        }

        return entity.getId();
    }
}