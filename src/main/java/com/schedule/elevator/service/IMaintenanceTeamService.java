package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.entity.MaintenanceTeam;

import java.util.List;

public interface IMaintenanceTeamService extends IService<MaintenanceTeam> {
    MaintenanceTeam getByTeamAndUnitId(String teamName, Long unitId);

    List<MaintenanceTeam> listByUnitId(Long unitId);

    long getOrCreateMaintenanceTeamId(MaintenanceTeam entity);
}
