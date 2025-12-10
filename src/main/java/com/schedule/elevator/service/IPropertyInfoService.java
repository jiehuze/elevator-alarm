package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.entity.PropertyInfo;

public interface IPropertyInfoService extends IService<PropertyInfo> {

    /**
     * 根据 unitCode 查询
     */
    PropertyInfo getByUnitCode(String unitCode);

    /**
     * 新增或更新（根据 unitCode 判断是否存在）
     */
    boolean saveOrUpdateByUnitCode(PropertyInfo entity);

    long getOrCreatePropertyId(PropertyInfo entity);
}
