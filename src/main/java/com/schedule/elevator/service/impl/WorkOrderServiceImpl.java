package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.WorkOrderMapper;
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
        query.like(StringUtils.isNotBlank(dto.getElevatorCode()), WorkOrder::getElevatorCode, dto.getElevatorCode());
        query.like(StringUtils.isNotBlank(dto.getRegisterCode()), WorkOrder::getRegisterCode, dto.getRegisterCode());
        query.like(StringUtils.isNotBlank(dto.getAlarmPersonName()), WorkOrder::getAlarmPersonName, dto.getAlarmPersonName());
        query.like(StringUtils.isNotBlank(dto.getAlarmPersonPhone()), WorkOrder::getAlarmPersonPhone, dto.getAlarmPersonPhone());
        query.like(StringUtils.isNotBlank(dto.getUsingUnit()), WorkOrder::getUsingUnit, dto.getUsingUnit());

        // 精确匹配字段
        query.eq(dto.getStatus() != null, WorkOrder::getStatus, dto.getStatus());
        query.eq(dto.getIsMajorIncident() != null, WorkOrder::getIsMajorIncident, dto.getIsMajorIncident());
        query.eq(dto.getOrderTypeId() != null, WorkOrder::getOrderTypeId, dto.getOrderTypeId());

        // 时间范围
        query.ge(dto.getAlarmTimeStart() != null, WorkOrder::getAlarmTime, dto.getAlarmTimeStart());
        query.le(dto.getAlarmTimeEnd() != null, WorkOrder::getAlarmTime, dto.getAlarmTimeEnd());
        query.ge(dto.getCreateTimeStart() != null, WorkOrder::getCreateTime, dto.getCreateTimeStart());
        query.le(dto.getCreateTimeEnd() != null, WorkOrder::getCreateTime, dto.getCreateTimeEnd());

        // 排序：按创建时间倒序
        query.orderByDesc(WorkOrder::getCreateTime);

        return this.page(page, query);
    }
}