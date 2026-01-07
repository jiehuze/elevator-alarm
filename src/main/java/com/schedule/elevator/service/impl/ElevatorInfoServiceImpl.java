package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.ElevatorInfoMapper;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.enums.ElevatorTypeEnum;
import com.schedule.elevator.service.IElevatorInfoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        queryWrapper.eq(elevatorInfoDTO.getUsageStatus() != null, ElevatorInfo::getUsageStatus, elevatorInfoDTO.getUsageStatus());
        queryWrapper.eq(elevatorInfoDTO.getUsingUnit() != null, ElevatorInfo::getUsingUnit, elevatorInfoDTO.getUsingUnit());
        queryWrapper.eq(elevatorInfoDTO.getUsingUnitId() != null, ElevatorInfo::getUsingUnitId, elevatorInfoDTO.getUsingUnitId());
        queryWrapper.eq(elevatorInfoDTO.getMaintenanceTeamId() != null, ElevatorInfo::getMaintenanceTeamId, elevatorInfoDTO.getMaintenanceTeamId());

        queryWrapper.orderByDesc(ElevatorInfo::getCreatedAt);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<ElevatorInfo> listElevators(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return this.list(); // 如果关键词为空，返回所有记录
        }

        LambdaQueryWrapper<ElevatorInfo> queryWrapper = new LambdaQueryWrapper<>();
        // 使用 or 连接多个 like 条件
        queryWrapper.and(wrapper -> wrapper
                .like(ElevatorInfo::getElevatorName, keyword)
                .or()
                .like(ElevatorInfo::getElevatorNo, keyword)
                .or()
                .like(ElevatorInfo::getElevatorType, keyword)
                .or()
                .like(ElevatorInfo::getRescueCode, keyword)
                .or()
                .like(ElevatorInfo::getDistrict, keyword)
                .or()
                .like(ElevatorInfo::getProjectName, keyword)
                .or()
                .like(ElevatorInfo::getLocation, keyword)
                .or()
                .like(ElevatorInfo::getUsingUnit, keyword)
        );

        return this.list(queryWrapper);
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
        queryWrapper.eq(dto.getCommunityId() != null, ElevatorInfo::getCommunityId, dto.getCommunityId());
        queryWrapper.eq(dto.getMaintenanceTeamId() != null, ElevatorInfo::getMaintenanceTeamId, dto.getMaintenanceTeamId());
        return this.count(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> countByElevatorType(SearchDTO searchDTO) {
        // 获取数据库中的统计结果
        List<Map<String, Object>> dbResults = baseMapper.countByElevatorType(searchDTO);

        // 创建包含所有电梯类型的映射
        Map<String, Integer> typeCountMap = new HashMap<>();

        // 初始化所有电梯类型为0
        for (ElevatorTypeEnum elevatorType : ElevatorTypeEnum.values()) {
            typeCountMap.put(elevatorType.getDescription(), 0);
        }

        // 填充数据库中的实际统计结果
        for (Map<String, Object> result : dbResults) {
            String elevatorType = (String) result.get("elevatorType");
            Long count = (Long) result.get("elevatorCount");
            typeCountMap.put(elevatorType, count.intValue());
        }

        // 构建最终结果
        List<Map<String, Object>> finalResults = new ArrayList<>();
        for (ElevatorTypeEnum elevatorType : ElevatorTypeEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("elevatorType", elevatorType.getDescription());
            item.put("elevatorCount", typeCountMap.get(elevatorType.getDescription()));
            finalResults.add(item);
        }

        return finalResults;
    }

    @Override
    public List<Map<String, Object>> countByDistrict(SearchDTO searchDTO) {
        return baseMapper.countByDistrict(searchDTO);
    }

    @Override
    public Map<String, Object> countNewElevators(SearchDTO searchDTO) {
        Map<String, Object> map = baseMapper.countNewElevators(searchDTO);

        // 使用Object类型接收，然后转换为Long
        Object totalObj = map.get("totalElevators");
        Object newObj = map.get("newElevators");

        Long totalElevators = totalObj != null ? ((Number) totalObj).longValue() : 0L;
        Long newElevators = newObj != null ? ((Number) newObj).longValue() : 0L;
        Long beforeElevators = totalElevators - newElevators;

        // 计算增长率：新增数/期初数量*100，保留2位小数
        double growthRate = 0.0;
        if (totalElevators != null && totalElevators > 0) {
            growthRate = Math.round((newElevators.doubleValue() / totalElevators) * 10000.0) / 100.0;
        }

        map.put("beforeElevators", beforeElevators);
        map.put("growthRate", growthRate);

        return map;
    }
}