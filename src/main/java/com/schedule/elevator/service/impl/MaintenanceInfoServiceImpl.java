package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.entity.MaintenanceInfo;
import com.schedule.elevator.dao.mapper.MaintenanceInfoMapper;
import com.schedule.elevator.service.IMaintenanceInfoService;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceInfoServiceImpl extends ServiceImpl<MaintenanceInfoMapper, MaintenanceInfo>
        implements IMaintenanceInfoService {
}