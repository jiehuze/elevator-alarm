package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.WorkOrderMapper;
import com.schedule.elevator.dto.HandleProgressDTO;
import com.schedule.elevator.dto.WorkOrderDTO;
import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.service.IWorkOrderService;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder>
        implements IWorkOrderService {
    @Override
    public Page<WorkOrder> queryByConditionsPage(WorkOrderDTO dto) {
        // 校验分页参数
        int current = (dto.getCurrent() == null || dto.getCurrent() < 1) ? 1 : dto.getCurrent();
        int size = (dto.getSize() == null || dto.getSize() < 1 || dto.getSize() > 100) ? 10 : dto.getSize();

        Page<WorkOrder> page = new Page<>(current, size);

        LambdaQueryWrapper<WorkOrder> query = new LambdaQueryWrapper<>();

        // 字符串字段：模糊查询（LIKE）
        query.like(StringUtils.isNotBlank(dto.getOrderNo()), WorkOrder::getOrderNo, dto.getOrderNo());
        query.like(StringUtils.isNotBlank(dto.getAlarmPersonName()), WorkOrder::getAlarmPersonName, dto.getAlarmPersonName());
        query.like(StringUtils.isNotBlank(dto.getAlarmPersonPhone()), WorkOrder::getAlarmPersonPhone, dto.getAlarmPersonPhone());

        query.like(StringUtils.isNotBlank(dto.getProjectName()), WorkOrder::getProjectName, dto.getProjectName());
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

        // 排序：按创建时间倒序
        query.orderByDesc(WorkOrder::getCreateTime);

        return this.page(page, query);
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
        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenanceUnitName()), WorkOrder::getMaintenanceUnitName, workOrder.getMaintenanceUnitName());
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

//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getAlarmSource()), WorkOrder::getAlarmSource, workOrder.getAlarmSource());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getAlarmPersonName()), WorkOrder::getAlarmPersonName, workOrder.getAlarmPersonName());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getAlarmPersonPhone()), WorkOrder::getAlarmPersonPhone, workOrder.getAlarmPersonPhone());
//        updateWrapper.set(workOrder.getAlarmTime() != null, WorkOrder::getAlarmTime, workOrder.getAlarmTime());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getElevatorAddress()), WorkOrder::getElevatorAddress, workOrder.getElevatorAddress());
//        updateWrapper.set(workOrder.getOrderType() != null, WorkOrder::getOrderType, workOrder.getOrderType());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getIncidentDescription()), WorkOrder::getIncidentDescription, workOrder.getIncidentDescription());
//        updateWrapper.set(workOrder.getRescueLevel() != null, WorkOrder::getRescueLevel, workOrder.getRescueLevel());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getRescueHotline()), WorkOrder::getRescueHotline, workOrder.getRescueHotline());
//        updateWrapper.set(workOrder.getInjuredCount() != null, WorkOrder::getInjuredCount, workOrder.getInjuredCount());
//        updateWrapper.set(workOrder.getTrappedCount() != null, WorkOrder::getTrappedCount, workOrder.getTrappedCount());
//        updateWrapper.set(workOrder.getSuspectedDeathCount() != null, WorkOrder::getSuspectedDeathCount, workOrder.getSuspectedDeathCount());
//        updateWrapper.set(workOrder.getStatus() != null, WorkOrder::getStatus, workOrder.getStatus());
//        updateWrapper.set(workOrder.getMajorIncident() != null, WorkOrder::getMajorIncident, workOrder.getMajorIncident());
//        updateWrapper.set(workOrder.getReported() != null, WorkOrder::getReported, workOrder.getReported());
//        updateWrapper.set(workOrder.getMedicalRescueStarted() != null, WorkOrder::getMedicalRescueStarted, workOrder.getMedicalRescueStarted());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getEmployeeId()), WorkOrder::getEmployeeId, workOrder.getEmployeeId()); // 员工ID
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenanceUnitName()), WorkOrder::getMaintenanceUnitName, workOrder.getMaintenanceUnitName());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenanceTeamName()), WorkOrder::getMaintenanceTeamName, workOrder.getMaintenanceTeamName());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenancePersonnelName()), WorkOrder::getMaintenancePersonnelName, workOrder.getMaintenancePersonnelName());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenancePersonnelPhone()), WorkOrder::getMaintenancePersonnelPhone, workOrder.getMaintenancePersonnelPhone());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getMaintenanceTeamLeaderPhone()), WorkOrder::getMaintenanceTeamLeaderPhone, workOrder.getMaintenanceTeamLeaderPhone());
//        updateWrapper.set(StringUtils.isNotBlank(workOrder.getRescueHotline()), WorkOrder::getRescueHotline, workOrder.getRescueHotline()); // 救援热线

        return update(workOrder, updateWrapper);
    }
}