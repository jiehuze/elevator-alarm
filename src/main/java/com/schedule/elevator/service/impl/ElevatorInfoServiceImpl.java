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

        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getElevatorName()), ElevatorInfo::getElevatorName, elevatorInfoDTO.getElevatorName());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getElevatorType()), ElevatorInfo::getElevatorType, elevatorInfoDTO.getElevatorType());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getElevatorNo()), ElevatorInfo::getElevatorNo, elevatorInfoDTO.getElevatorNo());
        queryWrapper.eq(elevatorInfoDTO.getMaintenanceUnitId() != null, ElevatorInfo::getMaintenanceUnitId, elevatorInfoDTO.getMaintenanceUnitId());
        queryWrapper.eq(StringUtils.isNotBlank(elevatorInfoDTO.getUsageStatus()), ElevatorInfo::getUsageStatus, elevatorInfoDTO.getUsageStatus());
        queryWrapper.eq(elevatorInfoDTO.getUsingUnit() != null, ElevatorInfo::getUsingUnit, elevatorInfoDTO.getUsingUnit());
        queryWrapper.eq(elevatorInfoDTO.getUsingUnitId() != null, ElevatorInfo::getUsingUnitId, elevatorInfoDTO.getUsingUnitId());
        queryWrapper.eq(elevatorInfoDTO.getMaintenanceTeamId() != null, ElevatorInfo::getMaintenanceTeamId, elevatorInfoDTO.getMaintenanceTeamId());

        queryWrapper.orderByDesc(ElevatorInfo::getCreatedAt);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean createElevatorInfo(ElevatorInfo elevatorInfo) throws Exception {
        if (elevatorInfo == null) {
            throw new IllegalArgumentException("电梯信息不能为空");
        }

        String rescueCode = elevatorInfo.getRescueCode();

        // 只有当 rescueCode 非空时才做唯一性校验
        if (StringUtils.isNotBlank(rescueCode)) {
            boolean exists = this.count(new LambdaQueryWrapper<ElevatorInfo>()
                    .eq(ElevatorInfo::getRescueCode, rescueCode.trim())) > 0;

            if (exists) {
                throw new RuntimeException("电梯救援码" + rescueCode + "已存在");
            }
        }

        // 执行插入
        boolean saved = this.save(elevatorInfo);
        if (!saved) {
            throw new RuntimeException("数据库插入失败");
        }

        return true;
    }

    @Override
    public Long count(ElevatorInfoDTO dto) {
        LambdaQueryWrapper<ElevatorInfo> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(StringUtils.isNotBlank(dto.getElevatorNo()), ElevatorInfo::getElevatorNo, dto.getElevatorNo());
        queryWrapper.eq(StringUtils.isNotBlank(dto.getElevatorName()), ElevatorInfo::getElevatorName, dto.getElevatorName());
        queryWrapper.eq(StringUtils.isNotBlank(dto.getElevatorType()), ElevatorInfo::getElevatorType, dto.getElevatorType());
        queryWrapper.eq(dto.getMaintenanceUnitId() != null, ElevatorInfo::getMaintenanceUnitId, dto.getMaintenanceUnitId());
        queryWrapper.eq(dto.getUsingUnitId() != null, ElevatorInfo::getUsingUnitId, dto.getUsingUnitId());
        queryWrapper.eq(dto.getMaintenanceTeamId() != null, ElevatorInfo::getMaintenanceTeamId, dto.getMaintenanceTeamId());
        return this.count(queryWrapper);
    }
}