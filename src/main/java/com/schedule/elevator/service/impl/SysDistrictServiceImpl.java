package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.SysDistrictMapper;
import com.schedule.elevator.dto.SysDistrictDTO;
import com.schedule.elevator.entity.SysDistrict;
import com.schedule.elevator.service.ISysDistrictService;
import org.springframework.stereotype.Service;

@Service
public class SysDistrictServiceImpl extends ServiceImpl<SysDistrictMapper, SysDistrict> 
        implements ISysDistrictService {

    @Override
    public Page<SysDistrict> queryPage(SysDistrictDTO sysDistrictDTO, int current, int size) {
        LambdaQueryWrapper<SysDistrict> queryWrapper = new LambdaQueryWrapper<>();
        
        // 动态条件查询
        queryWrapper.like(sysDistrictDTO.getDistrictCode() != null, 
                         SysDistrict::getDistrictCode, sysDistrictDTO.getDistrictCode())
                   .like(sysDistrictDTO.getDistrictName() != null, 
                         SysDistrict::getDistrictName, sysDistrictDTO.getDistrictName())
                   .eq(sysDistrictDTO.getDistrictLevel() != null, 
                       SysDistrict::getDistrictLevel, sysDistrictDTO.getDistrictLevel())
                   .eq(sysDistrictDTO.getEnabled() != null, 
                       SysDistrict::getEnabled, sysDistrictDTO.getEnabled())
                   .orderByAsc(SysDistrict::getSort);
        
        return this.page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public boolean saveOrUpdateSysDistrict(SysDistrict sysDistrict) {
        return this.saveOrUpdate(sysDistrict);
    }

    @Override
    public SysDistrict getById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean removeById(Long id) {
        return this.removeById(id);
    }

    @Override
    public SysDistrict getByDistrictCode(String districtCode) {
        LambdaQueryWrapper<SysDistrict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDistrict::getDistrictCode, districtCode);
        return this.getOne(queryWrapper);
    }
}
