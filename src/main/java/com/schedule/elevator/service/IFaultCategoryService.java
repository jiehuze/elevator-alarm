package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.entity.FaultCategory;

import java.util.List;

public interface IFaultCategoryService extends IService<FaultCategory> {

    /**
     * 获取完整的故障分类树形结构
     */
    List<FaultCategory> getFaultCategoryTree();
}