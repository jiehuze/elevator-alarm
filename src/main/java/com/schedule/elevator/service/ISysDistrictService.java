package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.SysDistrictDTO;
import com.schedule.elevator.entity.SysDistrict;

public interface ISysDistrictService extends IService<SysDistrict> {
    
    /**
     * 分页查询区域信息
     */
    Page<SysDistrict> queryPage(SysDistrictDTO sysDistrictDTO, int current, int size);
    
    /**
     * 保存或更新区域信息
     */
    boolean saveOrUpdateSysDistrict(SysDistrict sysDistrict);
    
    /**
     * 根据ID获取区域信息
     */
    SysDistrict getById(Long id);
    
    /**
     * 根据ID删除区域信息
     */
    boolean removeById(Long id);
    
    /**
     * 根据区域编码查询区域信息
     */
    SysDistrict getByDistrictCode(String districtCode);
}
