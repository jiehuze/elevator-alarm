package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.SafetyOfficerMapper;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.SafetyOfficer;
import com.schedule.elevator.service.ISafetyOfficerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SafetyOfficerServiceImpl extends ServiceImpl<SafetyOfficerMapper, SafetyOfficer>
        implements ISafetyOfficerService {

    private LambdaQueryWrapper<SafetyOfficer> buildQueryWrapper(SearchDTO dto) {
        LambdaQueryWrapper<SafetyOfficer> query = new LambdaQueryWrapper<>();
        query.eq(dto.getUsingUnitId() != null, SafetyOfficer::getUsingUnitId, dto.getUsingUnitId());
        query.eq(dto.getStatus() != null, SafetyOfficer::getStatus, dto.getStatus());
        query.like(StringUtils.isNotBlank(dto.getUsingUnit()), SafetyOfficer::getUsingUnit, dto.getUsingUnit());
        query.like(StringUtils.isNotBlank(dto.getSafetyOfficerPhone()), SafetyOfficer::getSafetyOfficerPhone, dto.getSafetyOfficerPhone());
        query.like(StringUtils.isNotBlank(dto.getSafetyOfficerName()), SafetyOfficer::getSafetyOfficerName, dto.getSafetyOfficerName());

        query.like(StringUtils.isNotBlank(dto.getSafetyOfficerName()), SafetyOfficer::getSafetyOfficerName, dto.getSafetyOfficerName());

        return query;
    }

    @Override
    public Page<SafetyOfficer> queryByConditionsPage(SearchDTO dto) {
        // 校验分页参数
        int current = (dto.getCurrent() == null || dto.getCurrent() < 1) ? 1 : dto.getCurrent();
        int size = (dto.getSize() == null || dto.getSize() < 1 || dto.getSize() > 100) ? 10 : dto.getSize();

        Page<SafetyOfficer> page = new Page<>(current, size);
        LambdaQueryWrapper<SafetyOfficer> query = this.buildQueryWrapper(dto);
        return page(page, query);
    }

    @Override
    public List<SafetyOfficer> queryByConditions(SearchDTO wrokOrderDTO) {
        LambdaQueryWrapper<SafetyOfficer> query = this.buildQueryWrapper(wrokOrderDTO);
        return list(query);
    }

    @Override
    public long getOrCreateSafetyOfficerId(SafetyOfficer entity) {
        // 1. 先查询是否已存在
        SafetyOfficer existing = this.getOne(new LambdaQueryWrapper<SafetyOfficer>()
                .eq(entity.getUsingUnitId() != null, SafetyOfficer::getUsingUnitId, entity.getUsingUnitId())
                .eq(SafetyOfficer::getSafetyOfficerPhone, entity.getSafetyOfficerPhone()));
//一个安全员可以在不同使用单位
//        if (existing != null && !existing.getUsingUnitId().equals(entity.getUsingUnitId())) {
//            System.out.println("存在的使用单位id:" + existing.getUsingUnitId() + "，使用单位id:" + entity.getUsingUnitId());
//            throw new RuntimeException("安全员:" + entity.getSafetyOfficerName() + "，在不同使用单位");
//        }

        if (existing != null) {
            return existing.getId();
        }

        boolean saved = this.save(entity);
        if (!saved) {
            throw new RuntimeException("维保单位插入失败");
        }

        return entity.getId();
    }

    @Override
    public List<SafetyOfficer> findByUsingUnitId(Long usingUnitId) {
        QueryWrapper<SafetyOfficer> wrapper = new QueryWrapper<>();
        wrapper.eq("using_unit_id", usingUnitId);
        return list(wrapper);
    }

    @Override
    public List<SafetyOfficer> findByStatus(Integer status) {
        QueryWrapper<SafetyOfficer> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        return list(wrapper);
    }

    @Override
    public SafetyOfficer findByUsingUnitIdAndPhone(Long usingUnitId, String phone) {
        QueryWrapper<SafetyOfficer> wrapper = new QueryWrapper<>();
        wrapper.eq("using_unit_id", usingUnitId)
                .eq("safety_officer_phone", phone);
        return getOne(wrapper);
    }

    @Override
    @Transactional
    public boolean updateStatusById(Long id, Integer status) {
        UpdateWrapper<SafetyOfficer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id)
                .set("status", status)
                .set("updated_at", LocalDateTime.now());
        return update(updateWrapper);
    }

    @Override
    public boolean existsByUnitIdAndPhone(Long usingUnitId, String phone, Long excludeId) {
        QueryWrapper<SafetyOfficer> wrapper = new QueryWrapper<>();
        wrapper.eq("using_unit_id", usingUnitId)
                .eq("safety_officer_phone", phone);

        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }

        return count(wrapper) > 0;
    }
}
