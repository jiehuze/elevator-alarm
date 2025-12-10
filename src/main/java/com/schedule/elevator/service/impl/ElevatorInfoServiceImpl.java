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
//                log.warn("救援码已存在，拒绝重复插入: {}", rescueCode);
                throw new RuntimeException("电梯救援码" + rescueCode + "已存在");
            }
        }

        // 执行插入
        boolean saved = this.save(elevatorInfo);
        if (!saved) {
//            log.error("电梯信息插入失败: {}", elevatorInfo);
            throw new RuntimeException("数据库插入失败");
        }

//        log.info("新增电梯成功，ID: {}, 救援码: {}", elevatorInfo.getId(), rescueCode);
        return true;
    }
}