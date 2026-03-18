package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.ElevatorInfoMapper;
import com.schedule.elevator.dto.*;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.enums.ElevatorTypeEnum;
import com.schedule.elevator.service.IElevatorInfoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElevatorInfoServiceImpl extends ServiceImpl<ElevatorInfoMapper, ElevatorInfo>
        implements IElevatorInfoService {

    private final ElevatorInfoMapper elevatorInfoMapper;

    public ElevatorInfoServiceImpl(ElevatorInfoMapper elevatorInfoMapper) {
        this.elevatorInfoMapper = elevatorInfoMapper;
    }

    @Override
    public ElevatorInfo searchElevatorInfo(SearchDTO searchDTO) {
        LambdaQueryWrapper<ElevatorInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getRescueCode()), ElevatorInfo::getRescueCode, searchDTO.getRescueCode());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getRegisterCode()), ElevatorInfo::getRegisterCode, searchDTO.getRegisterCode());
        return this.getOne(queryWrapper);
    }

    private LambdaQueryWrapper<ElevatorInfo> buildQueryWrapper(SearchDTO elevatorInfoDTO) {
        LambdaQueryWrapper<ElevatorInfo> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getElevatorName()), ElevatorInfo::getElevatorName, elevatorInfoDTO.getElevatorName());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getElevatorType()), ElevatorInfo::getElevatorType, elevatorInfoDTO.getElevatorType());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getElevatorNo()), ElevatorInfo::getElevatorNo, elevatorInfoDTO.getElevatorNo());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getUsingUnit()), ElevatorInfo::getUsingUnit, elevatorInfoDTO.getUsingUnit());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getDistrict()), ElevatorInfo::getDistrict, elevatorInfoDTO.getDistrict());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getProjectName()), ElevatorInfo::getProjectName, elevatorInfoDTO.getProjectName());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getRescueCode()), ElevatorInfo::getRescueCode, elevatorInfoDTO.getRescueCode());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getMaintenanceUnit()), ElevatorInfo::getMaintenanceUnit, elevatorInfoDTO.getMaintenanceUnit());
        queryWrapper.like(StringUtils.isNotBlank(elevatorInfoDTO.getRegisterCode()), ElevatorInfo::getRegisterCode, elevatorInfoDTO.getRegisterCode());
        queryWrapper.eq(elevatorInfoDTO.getMaintenanceUnitId() != null, ElevatorInfo::getMaintenanceUnitId, elevatorInfoDTO.getMaintenanceUnitId());
        queryWrapper.eq(elevatorInfoDTO.getUsageStatus() != null, ElevatorInfo::getUsageStatus, elevatorInfoDTO.getUsageStatus());
        queryWrapper.eq(elevatorInfoDTO.getUsingUnitId() != null, ElevatorInfo::getUsingUnitId, elevatorInfoDTO.getUsingUnitId());
        queryWrapper.eq(elevatorInfoDTO.getMaintenanceTeamId() != null, ElevatorInfo::getMaintenanceTeamId, elevatorInfoDTO.getMaintenanceTeamId());
        queryWrapper.like(elevatorInfoDTO.getMaintenancePersonnelId() != null, ElevatorInfo::getMaintenancePersonnelId, elevatorInfoDTO.getMaintenancePersonnelId());
        queryWrapper.eq(elevatorInfoDTO.getCommunityId() != null, ElevatorInfo::getCommunityId, elevatorInfoDTO.getCommunityId());
        queryWrapper.eq(elevatorInfoDTO.getSafetyOfficerId() != null, ElevatorInfo::getSafetyOfficerId, elevatorInfoDTO.getSafetyOfficerId());

        if (elevatorInfoDTO.getStartOperationDateStart() != null && elevatorInfoDTO.getStartOperationDateEnd() != null) {
            queryWrapper.between(ElevatorInfo::getOperationStartDate, elevatorInfoDTO.getStartOperationDateStart(), elevatorInfoDTO.getStartOperationDateEnd());
        }

        if (elevatorInfoDTO.getUnbound() != null && elevatorInfoDTO.getUnbound() == true) {
            queryWrapper.and(wrapper ->
                    wrapper.isNull(ElevatorInfo::getMaintenancePersonnelId)
                            .or()
                            .eq(ElevatorInfo::getMaintenancePersonnelId, 0)
            );
        }

        if (elevatorInfoDTO.getElevatorIds() != null) {
            queryWrapper.and(wrapper ->
                    wrapper.in(ElevatorInfo::getId, elevatorInfoDTO.getElevatorIds())
            );
        }

        if (StringUtils.isNotBlank(elevatorInfoDTO.getKeyword())) {
            queryWrapper.and(wrapper ->
                            wrapper.like(ElevatorInfo::getProjectName, elevatorInfoDTO.getKeyword())
//                            .or()
//                            .like(ElevatorInfo::getElevatorName, elevatorInfoDTO.getKeyword())
//                            .or()
//                            .like(ElevatorInfo::getElevatorNo, elevatorInfoDTO.getKeyword())
                                    .or()
                                    .like(ElevatorInfo::getDistrict, elevatorInfoDTO.getKeyword())
                                    .or()
                                    .like(ElevatorInfo::getRescueCode, elevatorInfoDTO.getKeyword())
            );
        }

        // 计算电梯运行时间到现在的年限，在最大和最小年限之间的数据，包含最大和最小
        if (elevatorInfoDTO.getServiceLifeMin() != null && elevatorInfoDTO.getServiceLifeMax() != null) {
            // 使用数据库函数计算运行年限（从运营开始日期到当前日期）
            queryWrapper.apply("TIMESTAMPDIFF(YEAR, operation_start_date, CURDATE()) BETWEEN {0} AND {1}",
                    elevatorInfoDTO.getServiceLifeMin(),
                    elevatorInfoDTO.getServiceLifeMax());
        }


        return queryWrapper;
    }

    @Override
    public IPage<ElevatorInfo> pageElevators(Page<ElevatorInfo> page, SearchDTO elevatorInfoDTO) {
        System.out.println("ele-----------: " + elevatorInfoDTO);
        LambdaQueryWrapper<ElevatorInfo> queryWrapper = buildQueryWrapper(elevatorInfoDTO);
        queryWrapper.orderByAsc(ElevatorInfo::getRescueCode);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<ElevatorInfo> listElevators(SearchDTO elevatorInfoDTO) {
        LambdaQueryWrapper<ElevatorInfo> queryWrapper = buildQueryWrapper(elevatorInfoDTO);
        queryWrapper.orderByAsc(ElevatorInfo::getRescueCode);

        return this.list(queryWrapper);
    }

    @Override
    public boolean createElevatorInfo(ElevatorInfo elevatorInfo) throws Exception {
        // 只有当 rescueCode 非空时才做唯一性校验
        if (StringUtils.isNotBlank(elevatorInfo.getRescueCode())) {
            boolean exists = this.count(new LambdaQueryWrapper<ElevatorInfo>()
                    .eq(ElevatorInfo::getRescueCode, elevatorInfo.getRescueCode().trim())) > 0;

            if (exists) {
                LambdaUpdateWrapper<ElevatorInfo> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ElevatorInfo::getRescueCode, elevatorInfo.getRescueCode().trim());
                return this.update(elevatorInfo, updateWrapper);
            } else {
                return this.save(elevatorInfo);
            }
        }

        return false;
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
    public List<ElevatorTypeStatsDTO> statsByElevatorType(SearchDTO searchDTO) {
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
        List<ElevatorTypeStatsDTO> finalResults = new ArrayList<>();
        for (ElevatorTypeEnum elevatorType : ElevatorTypeEnum.values()) {
            ElevatorTypeStatsDTO dto = new ElevatorTypeStatsDTO()
                    .setElevatorType(elevatorType.getDescription())
                    .setElevatorCount(typeCountMap.get(elevatorType.getDescription()) == null ? 0 : typeCountMap.get(elevatorType.getDescription()));
            finalResults.add(dto);
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

    @Override
    public List<ProjectTypeCountDTO> getProjectTypeStats(SearchDTO searchDTO) {
        return elevatorInfoMapper.countElevatorsByProjectType(searchDTO);
    }

    @Override
    public BrandElevatorStatisticsDTO getBrandElevatorStats(SearchDTO searchDTO) {
        BrandElevatorStatisticsDTO statisticsDTO = new BrandElevatorStatisticsDTO();

        List<BrandElevatorCountDTO> top5BrandElevatorCounts = elevatorInfoMapper.getTop5BrandElevatorCounts(searchDTO);
        BrandMarketAnalysisDTO brandMarketAnalysis = elevatorInfoMapper.getBrandMarketAnalysis(searchDTO);

        StringBuilder top5Brands = new StringBuilder();
        BigDecimal totals = BigDecimal.ZERO;
        for (int i = 0; i < top5BrandElevatorCounts.size(); i++) {
            BrandElevatorCountDTO brandElevatorCountDTO = top5BrandElevatorCounts.get(i);
            top5Brands.append(brandElevatorCountDTO.getBrandName());
            if (i < top5BrandElevatorCounts.size() - 1) {
                top5Brands.append(",");
            }
            totals = totals.add(new BigDecimal(brandElevatorCountDTO.getElevatorCount()));
        }
        statisticsDTO.setTop5Brands(top5Brands.toString());
        statisticsDTO.setTotalBrands(brandMarketAnalysis.getTotalBrands());
        statisticsDTO.setSmallBrandsCount(brandMarketAnalysis.getSmallBrandsCount());
        statisticsDTO.setSmallBrandPercentage(brandMarketAnalysis.getSmallBrandPercentage());
        statisticsDTO.setTotalBrandsAll(brandMarketAnalysis.getTotalBrandsAll());
        statisticsDTO.setTop5Percentage(totals.multiply(new BigDecimal(100)).divide(new BigDecimal(brandMarketAnalysis.getTotalBrandsAll()), 2, RoundingMode.HALF_UP));

        return statisticsDTO;
    }
}