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
        int current = (dto.getPageNum() == null || dto.getPageNum() < 1) ? 1 : dto.getPageNum();
        int size = (dto.getPageSize() == null || dto.getPageSize() < 1 || dto.getPageSize() > 100) ? 10 : dto.getPageSize();

        Page<WorkOrder> page = new Page<>(current, size);

        LambdaQueryWrapper<WorkOrder> query = new LambdaQueryWrapper<>();

        // 字符串字段：模糊查询（LIKE）
        query.like(StringUtils.isNotBlank(dto.getOrderNo()), WorkOrder::getOrderNo, dto.getOrderNo());
        query.like(StringUtils.isNotBlank(dto.getAlarmPersonName()), WorkOrder::getAlarmPersonName, dto.getAlarmPersonName());
        query.like(StringUtils.isNotBlank(dto.getAlarmPersonPhone()), WorkOrder::getAlarmPersonPhone, dto.getAlarmPersonPhone());

        // 精确匹配字段
        query.eq(dto.getStatus() != null, WorkOrder::getStatus, dto.getStatus());
        query.eq(dto.getOrderType() != null, WorkOrder::getOrderType, dto.getOrderType());

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
        if (workOrder.getEmployeeId() != null) {
            updateWrapper.set(WorkOrder::getEmployeeId, workOrder.getEmployeeId()); // 员工ID
        }
        if (workOrder.getMaintenanceUnitId() != null) {
            updateWrapper.set(WorkOrder::getMaintenanceUnitId, workOrder.getMaintenanceUnitId()); // 维修单位ID
        }
        if (workOrder.getMaintenanceTeamId() != null) {
            updateWrapper.set(WorkOrder::getMaintenanceTeamId, workOrder.getMaintenanceTeamId()); // 维修团队ID
        }
        if (workOrder.getRescueHotline() != null) {
            updateWrapper.set(WorkOrder::getRescueHotline, workOrder.getRescueHotline()); // 救援热线
        }
        if (workOrder.getStatus() != null) {
            updateWrapper.set(WorkOrder::getStatus, workOrder.getStatus()); //工单状态
        }
        if (workOrder.getMedicalRescueStarted() != null) {
            updateWrapper.set(WorkOrder::getMedicalRescueStarted, workOrder.getMedicalRescueStarted());// 是否启动医疗救援
        }

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
        if (workOrder.getOrderNo() != null) {
            updateWrapper.set(WorkOrder::getOrderNo, workOrder.getOrderNo());
        }
//        if (workOrder.getElevatorCode() != null) {
//            updateWrapper.set(WorkOrder::getElevatorCode, workOrder.getElevatorCode());
//        }
//        if (workOrder.getRegisterCode() != null) {
//            updateWrapper.set(WorkOrder::getRegisterCode, workOrder.getRegisterCode());
//        }
//        if (workOrder.getAlarmPersonName() != null) {
//            updateWrapper.set(WorkOrder::getAlarmPersonName, workOrder.getAlarmPersonName());
//        }
//        if (workOrder.getAlarmPersonPhone() != null) {
//            updateWrapper.set(WorkOrder::getAlarmPersonPhone, workOrder.getAlarmPersonPhone());
//        }
//        if (workOrder.getUsingUnit() != null) {
//            updateWrapper.set(WorkOrder::getUsingUnit, workOrder.getUsingUnit());
//        }
//        if (workOrder.getIsMajorIncident() != null) {
//            updateWrapper.set(WorkOrder::getIsMajorIncident, workOrder.getIsMajorIncident());
//        }
//        if (workOrder.getOrderType() != null) {
//            updateWrapper.set(WorkOrder::getOrderType, workOrder.getOrderType());
//        }
//        if (workOrder.getAlarmTime() != null) {
//            updateWrapper.set(WorkOrder::getAlarmTime, workOrder.getAlarmTime());
//        }

        return update(updateWrapper);
    }
}