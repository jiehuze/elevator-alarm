package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.MaintenancePersonnelMapper;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.MaintenancePersonnel;
import com.schedule.elevator.entity.MaintenanceTeam;
import com.schedule.elevator.service.IMaintenancePersonnelService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MaintenancePersonnelServiceImpl extends ServiceImpl<MaintenancePersonnelMapper, MaintenancePersonnel>
        implements IMaintenancePersonnelService {

    @Override
    public long getOrCreatePersonnelId(MaintenancePersonnel entity) {
        LambdaQueryWrapper<MaintenancePersonnel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaintenancePersonnel::getPhone, entity.getPhone());

        MaintenancePersonnel existing = this.getOne(queryWrapper);
        if (existing != null) {
            return existing.getId();
        }

        this.save(entity);
        return entity.getId();
    }

    @Override
    public long count(MaintenancePersonnel entity) {
        LambdaQueryWrapper<MaintenancePersonnel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(entity.getId() != null, MaintenancePersonnel::getId, entity.getId())
                .eq(entity.getMaintenanceUnitId() != null, MaintenancePersonnel::getMaintenanceUnitId, entity.getMaintenanceUnitId())
                .eq(entity.getMaintenanceTeamId() != null, MaintenancePersonnel::getMaintenanceTeamId, entity.getMaintenanceTeamId())
                .eq(StringUtils.hasText(entity.getPhone()), MaintenancePersonnel::getPhone, entity.getPhone());

        return this.count(queryWrapper);
    }

    @Override
    public boolean updateContentById(MaintenancePersonnel entity) {
        LambdaUpdateWrapper<MaintenancePersonnel> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.eq(entity.getId() != null, MaintenancePersonnel::getId, entity.getId());

        updateWrapper.set(entity.getMaintenanceUnitId() != null, MaintenancePersonnel::getMaintenanceUnitId, entity.getMaintenanceUnitId());
        updateWrapper.set(entity.getMaintenanceTeamId() != null, MaintenancePersonnel::getMaintenanceTeamId, entity.getMaintenanceTeamId());
        updateWrapper.set(entity.getName() != null, MaintenancePersonnel::getName, entity.getName());
        updateWrapper.set(entity.getStatus() != null, MaintenancePersonnel::getStatus, entity.getStatus());
        updateWrapper.set(entity.getPhone() != null, MaintenancePersonnel::getPhone, entity.getPhone());
        updateWrapper.set(StringUtils.hasText(entity.getCompany()), MaintenancePersonnel::getCompany, entity.getCompany());

        return update(updateWrapper);
    }

    @Override
    public long getOrCreateMaintenancePersonnelId(MaintenancePersonnel entity) {
        // 1. 先查询是否已存在
        MaintenancePersonnel existing = this.getOne(new LambdaQueryWrapper<MaintenancePersonnel>()
                .eq(MaintenancePersonnel::getPhone, entity.getPhone()));

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
    public IPage<MaintenancePersonnel> pagePersonnels(SearchDTO entity, int current, int size) {
        Page<MaintenancePersonnel> page = new Page<>(current, size);
        LambdaQueryWrapper<MaintenancePersonnel> queryWrapper = new LambdaQueryWrapper<>();
        if (entity.getNoMaintenanceTeam() != null && entity.getNoMaintenanceTeam() == true) {
            queryWrapper.eq(MaintenancePersonnel::getSubMaintenanceTeamId, 0);
        }

        if (entity.getLevel() != null) {
            if (entity.getLevel() == 1) {
                queryWrapper.eq(entity.getMaintenanceTeamId() != null, MaintenancePersonnel::getMaintenanceTeamId, entity.getMaintenanceTeamId());
            } else if (entity.getLevel() == 2) {
                queryWrapper.eq(entity.getMaintenanceTeamId() != null, MaintenancePersonnel::getSubMaintenanceTeamId, entity.getMaintenanceTeamId());
            }
        }

        queryWrapper.eq(entity.getId() != null, MaintenancePersonnel::getId, entity.getId())
                .eq(entity.getMaintenanceUnitId() != null, MaintenancePersonnel::getMaintenanceUnitId, entity.getMaintenanceUnitId())
                .eq(entity.getMaintenancePersonnelId() != null, MaintenancePersonnel::getId, entity.getMaintenancePersonnelId())
                .eq(entity.getStatus() != null, MaintenancePersonnel::getStatus, entity.getStatus())
                .orderByDesc(MaintenancePersonnel::getCreatedAt);

        return this.page(page, queryWrapper);
    }

    @Override
    public List<MaintenancePersonnel> listByTeamId(Long teamId, Integer level) {
        LambdaQueryWrapper<MaintenancePersonnel> queryWrapper = new LambdaQueryWrapper<>();

        if (level == 1) {
            queryWrapper.eq(MaintenancePersonnel::getMaintenanceTeamId, teamId);
        } else {
            queryWrapper.eq(MaintenancePersonnel::getSubMaintenanceTeamId, teamId);
        }

        return this.list(queryWrapper);
    }

    @Override
    public List<MaintenancePersonnel> listBySearchDTO(SearchDTO searchDTO) {
        LambdaQueryWrapper<MaintenancePersonnel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(searchDTO.getMaintenanceUnitId() != null, MaintenancePersonnel::getMaintenanceUnitId, searchDTO.getMaintenanceUnitId())
                .like(StringUtils.hasText(searchDTO.getMaintenanceUnit()), MaintenancePersonnel::getCompany, searchDTO.getMaintenanceUnit())
                .orderByAsc(MaintenancePersonnel::getCompany);

        return this.list(queryWrapper);
    }
}
