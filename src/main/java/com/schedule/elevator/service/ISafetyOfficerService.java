package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.PropertyInfo;
import com.schedule.elevator.entity.SafetyOfficer;
import com.schedule.elevator.entity.WorkOrder;

import java.util.List;

public interface ISafetyOfficerService extends IService<SafetyOfficer> {

    Page<SafetyOfficer> queryByConditionsPage(SearchDTO wrokOrderDTO);

    List<SafetyOfficer> queryByConditions(SearchDTO wrokOrderDTO);

    long getOrCreateSafetyOfficerId(SafetyOfficer entity);

    /**
     * 根据使用单位ID查询安全员列表
     */
    List<SafetyOfficer> findByUsingUnitId(Long usingUnitId);

    /**
     * 根据在职状态查询安全员列表
     */
    List<SafetyOfficer> findByStatus(Integer status);

    /**
     * 根据使用单位ID和电话号码查询安全员
     */
    SafetyOfficer findByUsingUnitIdAndPhone(Long usingUnitId, String phone);

    /**
     * 更新安全员在职状态
     */
    boolean updateStatusById(Long id, Integer status);

    /**
     * 检查电话号码在同一单位内是否已存在
     */
    boolean existsByUnitIdAndPhone(Long usingUnitId, String phone, Long excludeId);
}
