package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.PropertyInfoMapper;
import com.schedule.elevator.dto.PropertyInfoDTO;
import com.schedule.elevator.entity.PropertyInfo;
import com.schedule.elevator.service.IPropertyInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyInfoServiceImpl extends ServiceImpl<PropertyInfoMapper, PropertyInfo>
        implements IPropertyInfoService {

    @Override
    public PropertyInfo getByUnitCode(String unitCode) {
        return this.getOne(new LambdaQueryWrapper<PropertyInfo>()
                .eq(PropertyInfo::getUnitCode, unitCode));
    }

    @Transactional
    @Override
    public boolean saveOrUpdateByUnitCode(PropertyInfo entity) {
        PropertyInfo existing = getByUnitCode(entity.getUnitCode());
        if (existing != null) {
            entity.setId(existing.getId());
            return this.updateById(entity);
        } else {
            return this.save(entity);
        }
    }

    @Override
    public long getOrCreatePropertyId(PropertyInfo entity) {
        // 1. 先查询是否已存在
        PropertyInfo existing = this.getOne(new LambdaQueryWrapper<PropertyInfo>()
                .eq(PropertyInfo::getUsingUnit, entity.getUsingUnit())
                .eq(PropertyInfo::getSafetyOfficerPhone, entity.getSafetyOfficerPhone()));

        if (existing != null) {
//            log.debug("维保单位已存在，ID: {}", existing.getId());
//            entity.setId(existing.getId());
//            this.updateById(entity);
            return existing.getId();
        }

        boolean saved = this.save(entity);
        if (!saved) {
            throw new RuntimeException("维保单位插入失败");
        }

//        log.info("新增维保单位成功，ID: {}, 单位编码: {}", maintenance.getId(), unitCode);
        return entity.getId();
    }

    @Override
    public Page<PropertyInfo> queryByConditionsPage(PropertyInfoDTO dto) {
        int current = (dto.getCurrent() == null || dto.getCurrent() < 1) ? 1 : dto.getCurrent();
        int size = (dto.getSize() == null || dto.getSize() < 1 || dto.getSize() > 100) ? 10 : dto.getSize();

        Page<PropertyInfo> page = new Page<>(current, size);

        LambdaQueryWrapper<PropertyInfo> query = new LambdaQueryWrapper<>();
        query.eq(dto.getUnitCode() != null, PropertyInfo::getUnitCode, dto.getUnitCode());
        query.like(dto.getUsingUnit() != null, PropertyInfo::getUsingUnit, dto.getUsingUnit());
        query.like(StringUtils.isNotBlank(dto.getUsingUnitManager()), PropertyInfo::getUsingUnitManager, dto.getUsingUnitManager());
        query.like(StringUtils.isNotBlank(dto.getUsingUnitManagerPhone()), PropertyInfo::getUsingUnitManagerPhone, dto.getUsingUnitManagerPhone());

        // 排序：按创建时间倒序
        query.orderByDesc(PropertyInfo::getCreatedAt);

        System.out.println("++++++++++++" + query);
        return this.page(page, query);
    }
}
