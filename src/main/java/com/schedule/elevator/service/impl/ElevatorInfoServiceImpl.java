package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.ElevatorInfoMapper;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.service.IElevatorInfoService;
import org.springframework.stereotype.Service;

@Service
public class ElevatorInfoServiceImpl extends ServiceImpl<ElevatorInfoMapper, ElevatorInfo>
        implements IElevatorInfoService {

    @Override
    public IPage<ElevatorInfo> pageElevators(Page<ElevatorInfo> page, ElevatorInfoDTO elevatorInfoDTO) {
        LambdaQueryWrapper<ElevatorInfo> queryWrapper = new LambdaQueryWrapper<>();
        // 使用DTO参数进行条件查询
        if (StringUtils.isNotBlank(elevatorInfoDTO.getElevatorNo())) {
            queryWrapper.like(ElevatorInfo::getElevatorNo, elevatorInfoDTO.getElevatorNo());
        }
        queryWrapper.orderByDesc(ElevatorInfo::getCreatedAt);
        return this.page(page, queryWrapper);
    }
}