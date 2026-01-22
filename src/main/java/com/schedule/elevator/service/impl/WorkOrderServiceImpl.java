package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.WorkOrderMapper;
import com.schedule.elevator.dto.*;
import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.enums.ProjectTypeEnum;
import com.schedule.elevator.enums.WorkOrderStatusEnum;
import com.schedule.elevator.enums.WorkOrderTypeEnum;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IWorkOrderService;
import com.schedule.utils.DateUtils;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder>
        implements IWorkOrderService {

    @Autowired
    protected WorkOrderMapper workOrderMapper;

    @Autowired
    protected IElevatorInfoService elevatorInfoService;

    private LambdaQueryWrapper<WorkOrder> buildQueryWrapper(SearchDTO dto) {
        LambdaQueryWrapper<WorkOrder> query = new LambdaQueryWrapper<>();

        // 字符串字段：模糊查询（LIKE）
        query.like(StringUtils.isNotBlank(dto.getOrderNo()), WorkOrder::getOrderNo, dto.getOrderNo());
        query.like(StringUtils.isNotBlank(dto.getAlarmPersonName()), WorkOrder::getAlarmPersonName, dto.getAlarmPersonName());
        query.like(StringUtils.isNotBlank(dto.getAlarmPersonPhone()), WorkOrder::getAlarmPersonPhone, dto.getAlarmPersonPhone());

        query.like(StringUtils.isNotBlank(dto.getProjectName()), WorkOrder::getProjectName, dto.getProjectName());
        query.eq(StringUtils.isNotBlank(dto.getProjectType()), WorkOrder::getProjectType, dto.getProjectType());
        query.like(StringUtils.isNotBlank(dto.getElevatorAddress()), WorkOrder::getElevatorAddress, dto.getElevatorAddress());
        // 精确匹配字段
        query.eq(dto.getStatus() != null, WorkOrder::getStatus, dto.getStatus());
        query.eq(StringUtils.isNotBlank(dto.getOrderType()), WorkOrder::getOrderType, dto.getOrderType());
        query.eq(dto.getMajorIncident() != null, WorkOrder::getMajorIncident, dto.getMajorIncident());

        // 时间范围
        query.ge(dto.getAlarmTimeStart() != null, WorkOrder::getAlarmTime, dto.getAlarmTimeStart());
        query.le(dto.getAlarmTimeEnd() != null, WorkOrder::getAlarmTime, dto.getAlarmTimeEnd());
        query.ge(dto.getCreateTimeStart() != null, WorkOrder::getCreateTime, dto.getCreateTimeStart());
        query.le(dto.getCreateTimeEnd() != null, WorkOrder::getCreateTime, dto.getCreateTimeEnd());
        query.ne(dto.getUnfinished() != null, WorkOrder::getStatus, 99);
        query.eq(StringUtils.isNotBlank(dto.getDistrict()), WorkOrder::getDistrict, dto.getDistrict());
        query.eq(StringUtils.isNotBlank(dto.getEmployeeId()), WorkOrder::getEmployeeId, dto.getEmployeeId());
        query.eq(StringUtils.isNotBlank(dto.getRescueCode()), WorkOrder::getRescueCode, dto.getRescueCode());
        query.eq(dto.getMaintenanceUnitId() != null, WorkOrder::getMaintenanceUnitId, dto.getMaintenanceUnitId());
        query.like(StringUtils.isNotBlank(dto.getUsingUnit()), WorkOrder::getUsingUnit, dto.getUsingUnit());

        if (dto.getHistoryWorkOrder() != null) {
            query.eq(WorkOrder::getStatus, WorkOrderStatusEnum.CLOSED.getCode());
            query.notIn(WorkOrder::getOrderType, WorkOrderTypeEnum.COMPLAINT.getCode(), WorkOrderTypeEnum.CONSULTATION.getCode()); // 不包含 3,4,投诉和咨询
        }

        return query;
    }

    @Override
    public Page<WorkOrder> queryByConditionsPage(SearchDTO dto) {
        // 校验分页参数
        int current = (dto.getCurrent() == null || dto.getCurrent() < 1) ? 1 : dto.getCurrent();
        int size = (dto.getSize() == null || dto.getSize() < 1 || dto.getSize() > 100) ? 10 : dto.getSize();

        Page<WorkOrder> page = new Page<>(current, size);

        LambdaQueryWrapper<WorkOrder> query = buildQueryWrapper(dto);

        if (StringUtils.isNotBlank(dto.getRescueCodeOrder())) {
            if (dto.getRescueCodeOrder().equals("asc")) {
                query.orderByAsc(WorkOrder::getRescueCode);
            } else {
                query.orderByDesc(WorkOrder::getRescueCode);
            }
        }
        if (StringUtils.isNotBlank(dto.getTimeOrder())) {
            if (dto.getTimeOrder().equals("asc")) {
                query.orderByAsc(WorkOrder::getCreateTime);
            } else {
                query.orderByDesc(WorkOrder::getCreateTime);
            }
        } else {
            query.orderByDesc(WorkOrder::getCreateTime);
        }

        return this.page(page, query);
    }

    @Override
    public List<WorkOrder> queryByConditions(SearchDTO dto) {
        LambdaQueryWrapper<WorkOrder> query = buildQueryWrapper(dto);

        if (StringUtils.isNotBlank(dto.getRescueCodeOrder())) {
            if (dto.getRescueCodeOrder().equals("asc")) {
                query.orderByAsc(WorkOrder::getRescueCode);
            } else {
                query.orderByDesc(WorkOrder::getRescueCode);
            }
        }
        if (StringUtils.isNotBlank(dto.getTimeOrder())) {
            if (dto.getTimeOrder().equals("asc")) {
                query.orderByAsc(WorkOrder::getCreateTime);
            } else {
                query.orderByDesc(WorkOrder::getCreateTime);
            }
        } else {
            query.orderByDesc(WorkOrder::getCreateTime);
        }

        return this.list(query);
    }

    @Override
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        if (workOrder == null) {
            throw new IllegalArgumentException("工单信息不能为空");
        }

        boolean success = this.save(workOrder);
        if (!success) {
            throw new RuntimeException("工单保存失败");
        }

        return workOrder; // 已包含 ID 和自动填充字段
    }

    @Override
    public WorkOrder getWorkOrderByOrderNo(String orderNo) {
        return this.getOne(new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getOrderNo, orderNo));
    }

    @Override
    public Boolean createRescueInfo(WorkOrder workOrder) {
        LambdaUpdateWrapper<WorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WorkOrder::getId, workOrder.getId()); // 工单ID
        if (workOrder.getRescueLevel() != null) {
            updateWrapper.set(WorkOrder::getRescueLevel, workOrder.getRescueLevel()); //救援等级
        }
        if (workOrder.getStatus() != null) {
            updateWrapper.set(WorkOrder::getStatus, workOrder.getStatus()); //工单状态
        }
        if (workOrder.getMedicalRescueStarted() != null) {
            updateWrapper.set(WorkOrder::getMedicalRescueStarted, workOrder.getMedicalRescueStarted());// 是否启动医疗救援
        }
        if (workOrder.getMaintenanceUnitId() != null) {
            updateWrapper.set(WorkOrder::getMaintenanceUnitId, workOrder.getMaintenanceUnitId()); // 维修单位ID
        }
        if (workOrder.getMaintenanceTeamId() != null) {
            updateWrapper.set(WorkOrder::getMaintenanceTeamId, workOrder.getMaintenanceTeamId()); // 维修团队ID
        }
        if (workOrder.getMaintenancePersonnelId() != null) {
            updateWrapper.set(WorkOrder::getMaintenancePersonnelId, workOrder.getMaintenancePersonnelId()); // 维修人员ID
        }
        updateWrapper.set(StringUtils.isNotBlank(workOrder.getEmployeeId()), WorkOrder::getEmployeeId, workOrder.getEmployeeId()); // 员工ID
        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenanceUnit()), WorkOrder::getMaintenanceUnit, workOrder.getMaintenanceUnit());
        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenanceTeamName()), WorkOrder::getMaintenanceTeamName, workOrder.getMaintenanceTeamName());
        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenancePersonnelName()), WorkOrder::getMaintenancePersonnelName, workOrder.getMaintenancePersonnelName());
        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenancePersonnelPhone()), WorkOrder::getMaintenancePersonnelPhone, workOrder.getMaintenancePersonnelPhone());
        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenanceTeamLeaderPhone()), WorkOrder::getMaintenanceTeamLeaderPhone, workOrder.getMaintenanceTeamLeaderPhone());
        updateWrapper.set(StringUtils.isNotBlank(workOrder.getRescueHotline()), WorkOrder::getRescueHotline, workOrder.getRescueHotline()); // 救援热线

        return update(updateWrapper);
    }

    @Override
    public Boolean updateStatus(WorkOrder workOrder) {
        LambdaUpdateWrapper<WorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WorkOrder::getId, workOrder.getId());
        updateWrapper.set(WorkOrder::getStatus, workOrder.getStatus()); //救援等级

        return update(updateWrapper);
    }

    /**
     * 处理工单进度
     *
     * @param handleProgressDTO
     * @return
     */
    @Override
    public Boolean handleWorkOrder(HandleProgressDTO handleProgressDTO) {
        return null;
    }

    @Override
    public Boolean updateByOrderNo(WorkOrder workOrder) {
        LambdaUpdateWrapper<WorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WorkOrder::getOrderNo, workOrder.getOrderNo());
        return update(workOrder, updateWrapper);
    }

    @Override
    public List<SecondaryFaultStatsDTO> getOrdersByDuplicateRescueCode(SearchDTO searchDTO) {
        List<SecondaryFaultStatsDTO> secondaryFaultStats = workOrderMapper.getSecondaryFaultStats(searchDTO);
        int count = 0;
        String district = null;
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
        for (SecondaryFaultStatsDTO secondaryFaultStat : secondaryFaultStats) {
            if (district == null || !district.equals(secondaryFaultStat.getDistrict())) {
                count = 1;
            } else {
                count += 1;
            }
            secondaryFaultStat.setCount(count);
            district = secondaryFaultStat.getDistrict();
        }

        return secondaryFaultStats;
    }

    @Override
    public HashMap<String, DuplicateOrderDTO> getOrderMapByDuplicateRescueCode(SearchDTO searchDTO) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(
                WorkOrder::getRescueCode,
                baseMapper.selectList(
                                new LambdaQueryWrapper<WorkOrder>()
                                        .select(WorkOrder::getRescueCode)
                                        .ge(searchDTO.getCreateTimeStart() != null, WorkOrder::getCreateTime, searchDTO.getCreateTimeStart())  // 添加开始时间条件
                                        .le(searchDTO.getCreateTimeEnd() != null, WorkOrder::getCreateTime, searchDTO.getCreateTimeEnd())      // 添加结束时间条件
                                        .groupBy(WorkOrder::getRescueCode)
                                        .having("COUNT(*) >= 2")
                        ).stream()
                        .map(WorkOrder::getRescueCode)
                        .collect(Collectors.toList())
        );
        wrapper.orderByAsc(WorkOrder::getDistrict);

        List<WorkOrder> list = list(wrapper);

        HashMap<String, DuplicateOrderDTO> map = new HashMap<>();
        for (WorkOrder workOrder : list) {
            DuplicateOrderDTO dto = map.get(workOrder.getRescueCode());
            if (dto == null) {
                ArrayList<WorkOrder> workOrders = new ArrayList<>();
                dto = new DuplicateOrderDTO().setRescueCode(workOrder.getRescueCode()).setRegisterCode(workOrder.getRegisterCode()).setCount(1l).setDistrict(workOrder.getDistrict());
                dto.setList(workOrders);
                map.put(workOrder.getRescueCode(), dto);
            } else {
                dto.setCount(dto.getCount() + 1);
            }

            dto.getList().add(workOrder);
        }

        return map;
    }

    @Override
    public WorkOrderStatisticsDTO getWorkOrderStatisticsByCondition(SearchDTO searchDTO) {
        WorkOrderStatisticsDTO dto = workOrderMapper.getWorkOrderStatisticsByCondition(searchDTO);
        dto.setMonth(DateUtils.format(searchDTO.getCreateTimeStart(), DateUtils.DATE_PATTERN) + "~" + DateUtils.format(searchDTO.getCreateTimeEnd(), DateUtils.DATE_PATTERN));
        return dto;
    }

    @Override
    public List<WorkOrderStatisticsDTO> getWorkOrderStatsForMonth(SearchDTO searchDTO) {
        //从一月到12月，并统计每个季度
        ArrayList<WorkOrderStatisticsDTO> workOrderStatisticsDTOS = new ArrayList<>();
        WorkOrderStatisticsDTO endYearDto = new WorkOrderStatisticsDTO();
        endYearDto.setMonth("合计");

        int year = searchDTO.getCreateTimeStart().getYear();
        Map<Integer, LocalDateTime[]> ranges = DateUtils.getMonthlyAndQuarterlyRanges(year);
        for (int i = 1; i <= 12; i++) {
            LocalDateTime[] lt = ranges.get(i);
            searchDTO.setCreateTimeStart(lt[0]);
            searchDTO.setCreateTimeEnd(lt[1]);
            WorkOrderStatisticsDTO result = workOrderMapper.getWorkOrderStatisticsByCondition(searchDTO);
            result.setMonth(i + "月");
            workOrderStatisticsDTOS.add(result);

            if (i == 3 || i == 6 || i == 9 || i == 12) {
                LocalDateTime[] llt = ranges.get(12 + i / 3);
                searchDTO.setCreateTimeStart(llt[0]);
                searchDTO.setCreateTimeEnd(llt[1]);
                WorkOrderStatisticsDTO lltResult = workOrderMapper.getWorkOrderStatisticsByCondition(searchDTO);
                if (i == 3) {
                    lltResult.setMonth("一季度");
                } else if (i == 6) {
                    lltResult.setMonth("二季度");
                } else if (i == 9) {
                    lltResult.setMonth("三季度");
                } else if (i == 12) {
                    lltResult.setMonth("四季度");
                }
                workOrderStatisticsDTOS.add(lltResult);

                endYearDto.setTotalEvents(util.addLongValues(endYearDto.getTotalEvents(), lltResult.getTotalEvents()));
                endYearDto.setOtherEvents(util.addLongValues(endYearDto.getOtherEvents(), lltResult.getOtherEvents()));
                endYearDto.setTrappedEvents(util.addLongValues(endYearDto.getTrappedEvents(), lltResult.getTrappedEvents()));
                endYearDto.setNonTrappedEvents(util.addLongValues(endYearDto.getNonTrappedEvents(), lltResult.getNonTrappedEvents()));
                endYearDto.setRescuedPeople(util.addLongValues(endYearDto.getRescuedPeople(), lltResult.getRescuedPeople()));

                if (lltResult.getAvgArrivalTimeForTrapped() != 0) {
                    if (endYearDto.getAvgArrivalTimeForTrapped() == 0) {
                        endYearDto.setAvgArrivalTimeForTrapped(lltResult.getAvgArrivalTimeForTrapped());
                    } else {
                        endYearDto.setAvgArrivalTimeForTrapped(new BigDecimal(
                                util.addDoubleValues(endYearDto.getAvgArrivalTimeForTrapped(), lltResult.getAvgArrivalTimeForTrapped()))
                                .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP)
                                .doubleValue());
                    }
                }
                if (lltResult.getAvgArrivalTimeForNonTrapped() != 0) {
                    if (endYearDto.getAvgArrivalTimeForNonTrapped() == 0) {
                        endYearDto.setAvgArrivalTimeForNonTrapped(lltResult.getAvgArrivalTimeForNonTrapped());
                    } else {
                        endYearDto.setAvgArrivalTimeForNonTrapped(new BigDecimal(
                                util.addDoubleValues(endYearDto.getAvgArrivalTimeForNonTrapped(), lltResult.getAvgArrivalTimeForNonTrapped()))
                                .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP)
                                .doubleValue());
                    }
                }
                if (lltResult.getAvgRepairDuration() != 0) {
                    if (endYearDto.getAvgRepairDuration() == 0) {
                        endYearDto.setAvgRepairDuration(lltResult.getAvgRepairDuration());
                    } else {
                        endYearDto.setAvgRepairDuration(new BigDecimal(
                                util.addDoubleValues(endYearDto.getAvgRepairDuration(), lltResult.getAvgRepairDuration()))
                                .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP)
                                .doubleValue());
                    }
                }
                if (lltResult.getAvgRescueDuration() != 0) {
                    if (endYearDto.getAvgRescueDuration() == 0) {
                        endYearDto.setAvgRescueDuration(lltResult.getAvgRescueDuration());
                    } else {
                        endYearDto.setAvgRescueDuration(new BigDecimal(
                                util.addDoubleValues(endYearDto.getAvgRescueDuration(), lltResult.getAvgRescueDuration()))
                                .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP)
                                .doubleValue());
                    }
                }
            }
        }

        workOrderStatisticsDTOS.add(endYearDto);

        return workOrderStatisticsDTOS;
    }

    @Override
    public List<TimeSlotStatsDTO> getFaultStatsByTimeSlot(SearchDTO searchDTO) {
        // 1. 获取原始数据
        List<TimeSlotStatsDTO> rawList = workOrderMapper.getFaultStatsByTimeSlot(searchDTO);

        // 2. 计算总故障数（用于计算故障率）
        int totalFaults = rawList.stream()
                .mapToInt(TimeSlotStatsDTO::getCount)
                .sum();

        // 3. 构建完整时间段（0-2, 2-4, ..., 22-24）
        Map<String, TimeSlotStatsDTO> fullMap = new LinkedHashMap<>();
        for (int i = 0; i < 24; i += 2) {
            String slot = i + "-" + (i + 2);
            TimeSlotStatsDTO stat = new TimeSlotStatsDTO();
            stat.setTimeSlot(slot);
            stat.setCount(0);
            stat.setTrappedCount(0);
            stat.setNonTrappedCount(0);
            stat.setOtherCount(0);
            stat.setFailureRate("0.0%");
            fullMap.put(slot, stat);
        }

        // 4. 更新有数据的时段
        for (TimeSlotStatsDTO item : rawList) {
            TimeSlotStatsDTO existingStat = fullMap.get(item.getTimeSlot());
            if (existingStat != null) {
                existingStat.setCount(item.getCount());
                existingStat.setTrappedCount(item.getTrappedCount());
                existingStat.setNonTrappedCount(item.getNonTrappedCount());
                existingStat.setOtherCount(item.getOtherCount());

                // 计算故障率：当前时段故障数 * 100 / 总故障数
                BigDecimal rateDecimal = totalFaults > 0
                        ? BigDecimal.valueOf(item.getCount() * 100.0 / totalFaults)
                        : BigDecimal.ZERO;
                String rate = rateDecimal.setScale(2, RoundingMode.HALF_UP).toString();
                existingStat.setFailureRate(rate + "%"); // 保留两位小数
            }
        }

        // 5. 转为 List 返回（保持顺序）
        return new ArrayList<>(fullMap.values());
    }

    @Override
    public List<TimeConsumptionStatsDTO> getTimeConsumptionStats(SearchDTO searchDTO) {
        List<TimeConsumptionStatsDTO> timeConsumptionStats = workOrderMapper.getArriveTimeConsumptionStats(searchDTO);
        TimeConsumptionStatsDTO totalTimeConsumptionStats = workOrderMapper.getTotalArriveTimeConsumptionStats(searchDTO);
        List<TimeConsumptionStatsDTO> rescueTimeConsumptionStats = workOrderMapper.getRescueTimeConsumptionStats(searchDTO);
        Integer trappedRescueCount = workOrderMapper.getTrappedRescueCount(searchDTO);
        totalTimeConsumptionStats.setTrappedRescueCount(trappedRescueCount);

        System.out.println("--------救援：" + rescueTimeConsumptionStats.size() + "   " + rescueTimeConsumptionStats);
        System.out.println("--------困人：" + timeConsumptionStats.size() + "   " + timeConsumptionStats);
        for (int i = 0; i < timeConsumptionStats.size(); i++) {
            timeConsumptionStats.get(i).setTrappedRescueCount(rescueTimeConsumptionStats.get(i).getTrappedRescueCount());
        }
        timeConsumptionStats.add(totalTimeConsumptionStats);

        return timeConsumptionStats;
    }

    @Override
    public RescueLevelStatsDTO getRescueLevelStats(SearchDTO searchDTO) {
        RescueLevelStatsDTO stats = workOrderMapper.getRescueLevelStats(searchDTO);

        // 防止数据库返回 null（虽然 SUM/COUNT 通常不会）
        if (stats == null) {
            stats = new RescueLevelStatsDTO();
        }
        stats.setLevel1(stats.getLevel1() == null ? 0 : stats.getLevel1());
        stats.setLevel2(stats.getLevel2() == null ? 0 : stats.getLevel2());
        stats.setLevel3(stats.getLevel3() == null ? 0 : stats.getLevel3());
        stats.setTotal(stats.getTotal() == null ? 0 : stats.getTotal());

        return stats;
    }

    @Override
    public ProjectTypeStatItemDTO getProjectTypeStats(SearchDTO searchDTO) {
        List<ProjectTypeEnum> allTypes = Arrays.asList(ProjectTypeEnum.values());

        // 查询数据库中各 code 的数量
        List<ProjectTypeCountDTO> faultResults = workOrderMapper.getProjectTypeCounts(searchDTO);
        List<ProjectTypeCountDTO> results = elevatorInfoService.getProjectTypeStats(searchDTO);

        // 构建 countMap
        Map<String, ProjectTypeCountDTO> projectTypeMap = new HashMap<>();
        Long faultTotal = 0l;
        long total = 0l;
        for (ProjectTypeCountDTO item : faultResults) {
            Long faultCount = item.getFaultCount() == null ? 0 : item.getFaultCount();
            projectTypeMap.put(item.getProjectCode(), item);
            faultTotal += faultCount;
        }
        for (ProjectTypeCountDTO item : results) {
            Long count = item.getCount() == null ? 0 : item.getCount();
            ProjectTypeCountDTO countDTO = projectTypeMap.get(item.getProjectCode());
            if (countDTO != null) {
                projectTypeMap.get(item.getProjectCode()).setCount(count);
            } else {
                item.setFaultCount(0l);
                projectTypeMap.put(item.getProjectCode(), item);
            }
            total += count;
        }

        // 补全所有类型
        List<ProjectTypeCountDTO> result = new ArrayList<>();

        for (ProjectTypeEnum type : allTypes) {
            BigDecimal percentage = BigDecimal.ZERO;
            ProjectTypeCountDTO projectTypeCountDTO = projectTypeMap.get(type.getCode());
            if (projectTypeCountDTO != null) {
                if (faultTotal > 0 && projectTypeCountDTO.getFaultCount() != null) {
                    percentage = BigDecimal.valueOf(projectTypeCountDTO.getFaultCount())
                            .divide(BigDecimal.valueOf(faultTotal), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
                }
                projectTypeCountDTO.setProjectCode(type.getCode()).setProjectName(type.getDescription()).setFaultPercentage(percentage);

                percentage = BigDecimal.ZERO;
                if (total > 0) {
                    percentage = BigDecimal.valueOf(projectTypeCountDTO.getCount())
                            .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
                }
                projectTypeCountDTO.setPercentage(percentage);
            } else {
                projectTypeCountDTO = new ProjectTypeCountDTO();
                projectTypeCountDTO.setProjectCode(type.getCode())
                        .setProjectName(type.getDescription())
                        .setCount(0l)
                        .setFaultCount(0l)
                        .setPercentage(percentage)
                        .setFaultPercentage(percentage);
            }
            result.add(projectTypeCountDTO);
        }

        ProjectTypeStatItemDTO projectTypeStatItemDTO = new ProjectTypeStatItemDTO();
        projectTypeStatItemDTO.setProjectTypeCounts(result);
        projectTypeStatItemDTO.setFaultTotal(faultTotal);
        projectTypeStatItemDTO.setTotal(total);

        return projectTypeStatItemDTO;
    }

    @Override
    public List<OvertimeWorkOrderDTO> getOvertimeWorkOrders(SearchDTO searchDTO) {
        return workOrderMapper.getOvertimeWorkOrders(searchDTO);
    }

    @Override
    public List<DistrictStatisticsDTO> getDistrictStatistics(SearchDTO searchDTO) {
        return workOrderMapper.getDistrictStatistics(searchDTO);
    }

    @Override
    public List<MaintenanceUnitFaultRateDTO> getMaintenanceUnitFaultRate(SearchDTO searchDTO) {
        return workOrderMapper.getMaintenanceUnitFaultRate(searchDTO);
    }

    @Override
    public List<UsingUnitFaultRateDTO> getUsingUnitFaultRate(SearchDTO searchDTO) {
        return workOrderMapper.getUsingUnitFaultRate(searchDTO);
    }

    @Override
    public List<ElevatorBrandFaultRateDTO> getElevatorBrandFaultRate(SearchDTO searchDTO) {
        return workOrderMapper.getElevatorBrandFaultRate(searchDTO);
    }

    @Override
    public List<ElevatorAgeStatisticsDTO> getElevatorAgeStatistics(SearchDTO searchDTO) {
        return workOrderMapper.getElevatorAgeStatistics(searchDTO);
    }
}