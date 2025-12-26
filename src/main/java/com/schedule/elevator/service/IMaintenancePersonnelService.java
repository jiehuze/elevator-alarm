package com.schedule.elevator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.entity.MaintenancePersonnel;

public interface IMaintenancePersonnelService extends IService<MaintenancePersonnel> {

    /**
     * 分页查询维保人员信息
     */
    IPage<MaintenancePersonnel> pagePersonnels(MaintenancePersonnel entity, int current, int size);

    /**
     * 根据手机号获取或创建维保人员
     */
    long getOrCreatePersonnelId(MaintenancePersonnel entity);
}
