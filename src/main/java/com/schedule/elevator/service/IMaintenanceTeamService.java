package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.MaintenanceTeam;

import java.util.List;

public interface IMaintenanceTeamService extends IService<MaintenanceTeam> {
    List<MaintenanceTeam> getByTeamAndUnitId(String teamName, Long unitId);

    List<MaintenanceTeam> listByUnitId(Long unitId);

    List<MaintenanceTeam> listByDt(SearchDTO mt);

    Page<MaintenanceTeam> page(MaintenanceTeam mt, int current, int size);

    long getOrCreateMaintenanceTeamId(MaintenanceTeam entity);
}
