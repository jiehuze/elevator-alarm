package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.MaintenanceTeamMapper;
import com.schedule.elevator.entity.MaintenanceTeam;
import com.schedule.elevator.entity.MaintenanceUnit;
import com.schedule.elevator.service.IMaintenanceTeamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceTeamServiceImpl extends ServiceImpl<MaintenanceTeamMapper, MaintenanceTeam>
        implements IMaintenanceTeamService {

    @Override
    public MaintenanceTeam getByTeamAndUnitId(String teamName, Long unitId) {
        return this.getOne(new LambdaQueryWrapper<MaintenanceTeam>()
                .eq(MaintenanceTeam::getTeamName, teamName)
                .eq(MaintenanceTeam::getMaintenanceUnitId, unitId));
    }

    @Override
    public List<MaintenanceTeam> listByUnitId(Long unitId) {
        return this.list(new LambdaQueryWrapper<MaintenanceTeam>()
                .eq(MaintenanceTeam::getMaintenanceUnitId, unitId));
    }

    @Override
    public long getOrCreateMaintenanceTeamId(MaintenanceTeam entity) {
        // 1. 先查询是否已存在
        MaintenanceTeam existing = this.getOne(new LambdaQueryWrapper<MaintenanceTeam>()
                .eq(MaintenanceTeam::getTeamName, entity.getTeamName())
                .eq(MaintenanceTeam::getMaintenanceUnitId, entity.getMaintenanceUnitId()));

        if (existing != null) {
//            log.debug("维保单位已存在，ID: {}", existing.getId());
            return existing.getId();
        }

        boolean saved = this.save(entity);
        if (!saved) {
            throw new RuntimeException("维保单位插入失败");
        }

//        log.info("新增维保单位成功，ID: {}, 单位编码: {}", maintenance.getId(), unitCode);
        return entity.getId();
    }
}