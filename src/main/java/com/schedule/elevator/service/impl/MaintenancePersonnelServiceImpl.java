package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.MaintenancePersonnelMapper;
import com.schedule.elevator.entity.MaintenancePersonnel;
import com.schedule.elevator.service.IMaintenancePersonnelService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MaintenancePersonnelServiceImpl extends ServiceImpl<MaintenancePersonnelMapper, MaintenancePersonnel>
        implements IMaintenancePersonnelService {

    @Override
    public long getOrCreatePersonnelId(MaintenancePersonnel entity) {
        LambdaQueryWrapper<MaintenancePersonnel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaintenancePersonnel::getPhone, entity.getPhone());

        MaintenancePersonnel existing = this.getOne(queryWrapper);
        if (existing != null) {
            return existing.getId();
        }

        this.save(entity);
        return entity.getId();
    }

    @Override
    public long count(MaintenancePersonnel entity) {
        LambdaQueryWrapper<MaintenancePersonnel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(entity.getId() != null, MaintenancePersonnel::getId, entity.getId())
                .eq(entity.getMaintenanceUnitId() != null, MaintenancePersonnel::getMaintenanceUnitId, entity.getMaintenanceUnitId())
                .eq(entity.getMaintenanceTeamId() != null, MaintenancePersonnel::getMaintenanceTeamId, entity.getMaintenanceTeamId())
                .eq(StringUtils.hasText(entity.getPhone()), MaintenancePersonnel::getPhone, entity.getPhone());

        return this.count(queryWrapper);
    }

    @Override
    public IPage<MaintenancePersonnel> pagePersonnels(MaintenancePersonnel entity, int current, int size) {
        Page<MaintenancePersonnel> page = new Page<>(current, size);
        LambdaQueryWrapper<MaintenancePersonnel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(entity.getId() != null, MaintenancePersonnel::getId, entity.getId())
                .eq(entity.getMaintenanceUnitId() != null, MaintenancePersonnel::getMaintenanceUnitId, entity.getMaintenanceUnitId())
                .eq(entity.getMaintenanceTeamId() != null, MaintenancePersonnel::getMaintenanceTeamId, entity.getMaintenanceTeamId())
                .eq(StringUtils.hasText(entity.getPhone()), MaintenancePersonnel::getPhone, entity.getPhone())
                .eq(StringUtils.hasText(entity.getName()), MaintenancePersonnel::getName, entity.getName())
                .eq(entity.getStatus() != null, MaintenancePersonnel::getStatus, entity.getStatus())
                .orderByDesc(MaintenancePersonnel::getCreatedAt);

        return this.page(page, queryWrapper);
    }
}
