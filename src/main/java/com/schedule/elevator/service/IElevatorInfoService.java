package com.schedule.elevator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.entity.ElevatorInfo;

public interface IElevatorInfoService extends IService<ElevatorInfo> {

    IPage<ElevatorInfo> pageElevators(Page<ElevatorInfo> page, ElevatorInfoDTO elevatorInfoDTO);

    boolean createElevatorInfo(ElevatorInfo elevatorInfo) throws Exception;

    Long count(ElevatorInfoDTO dto);
}