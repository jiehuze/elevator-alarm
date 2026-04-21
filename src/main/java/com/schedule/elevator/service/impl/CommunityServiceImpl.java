package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.CommunityMapper;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.Community;
import com.schedule.elevator.service.ICommunityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements ICommunityService {

    @Override
    public long getOrCreateCommunityId(Community entity) {
        entity.setProjectName(StringUtils.trim(entity.getProjectName()));

        LambdaQueryWrapper<Community> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(StringUtils.isNotBlank(entity.getAddress()), Community::getAddress, entity.getAddress());
        queryWrapper.eq(StringUtils.isNoneBlank(entity.getProjectName()), Community::getProjectName, entity.getProjectName());
        queryWrapper.eq(StringUtils.isNotBlank(entity.getDistrict()), Community::getDistrict, entity.getDistrict());
        Community one = this.getOne(queryWrapper);

        if (one != null) {
            this.update(entity, new LambdaQueryWrapper<Community>().eq(Community::getId, one.getId()));
            return one.getId();
        } else {
            boolean save = this.save(entity);
            if (save) {
                return entity.getId();
            }
        }
        return -1;
    }

    @Override
    public IPage<Community> pageCommunities(Page<Community> page, Community searchDTO) {

        LambdaQueryWrapper<Community> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getAddress()), Community::getAddress, searchDTO.getAddress())
                .like(StringUtils.isNotBlank(searchDTO.getProjectName()), Community::getProjectName, searchDTO.getProjectName())
                .eq(StringUtils.isNotBlank(searchDTO.getDistrict()), Community::getDistrict, searchDTO.getDistrict())
                .like(StringUtils.isNotBlank(searchDTO.getSafetyOfficerName()), Community::getSafetyOfficerName, searchDTO.getSafetyOfficerName())
                .eq(searchDTO.getId() != null, Community::getId, searchDTO.getId())
                .eq(StringUtils.isNotBlank(searchDTO.getProjectType()), Community::getProjectType, searchDTO.getProjectType());

        // 根据维保单位ID查询对应的小区（使用子查询）
        if (searchDTO.getMaintenanceUnitId() != null) {
            queryWrapper.exists(
                    "SELECT 1 FROM elevator e WHERE e.community_id = communities.id AND e.maintenance_unit_id = {0} AND e.community_id IS NOT NULL",
                    searchDTO.getMaintenanceUnitId()
            );
        }

        queryWrapper.orderByDesc(Community::getCreatedAt);

        return this.page(page, queryWrapper);
    }

    @Override
    public List<Community> listCommunities(SearchDTO searchDTO) {
        LambdaQueryWrapper<Community> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getProjectName()), Community::getProjectName, searchDTO.getProjectName())
                .eq(StringUtils.isNotBlank(searchDTO.getDistrict()), Community::getDistrict, searchDTO.getDistrict())
                .like(StringUtils.isNotBlank(searchDTO.getSafetyOfficerName()), Community::getSafetyOfficerName, searchDTO.getSafetyOfficerName())
                .eq(searchDTO.getCommunityId() != null, Community::getId, searchDTO.getCommunityId())
                .eq(StringUtils.isNotBlank(searchDTO.getProjectType()), Community::getProjectType, searchDTO.getProjectType());

        return list(queryWrapper);
    }
}
