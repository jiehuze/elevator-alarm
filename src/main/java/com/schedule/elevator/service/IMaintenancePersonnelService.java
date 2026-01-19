package com.schedule.elevator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.MaintenancePersonnel;
import com.schedule.elevator.entity.MaintenanceTeam;

import java.util.List;

public interface IMaintenancePersonnelService extends IService<MaintenancePersonnel> {

    /**
     * 分页查询维保人员信息
     */
    IPage<MaintenancePersonnel> pagePersonnels(SearchDTO entity, int current, int size);

    List<MaintenancePersonnel> listByTeamId(Long teamId, Integer level);

    List<MaintenancePersonnel> listBySearchDTO(SearchDTO searchDTO);

    /**
     * 根据手机号获取或创建维保人员
     */
    long getOrCreatePersonnelId(MaintenancePersonnel entity);

    long count(MaintenancePersonnel entity);

    boolean updateContentById(MaintenancePersonnel entity);

    long getOrCreateMaintenancePersonnelId(MaintenancePersonnel entity);
}
