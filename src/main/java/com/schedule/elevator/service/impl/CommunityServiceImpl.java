package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.CommunityMapper;
import com.schedule.elevator.entity.Community;
import com.schedule.elevator.service.ICommunityService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements ICommunityService {

    @Override
    public long getOrCreateCommunityId(Community entity) {
        LambdaQueryWrapper<Community> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(entity.getAddress()), Community::getAddress, entity.getAddress());
        queryWrapper.eq(StringUtils.hasText(entity.getProjectName()), Community::getProjectName, entity.getProjectName());
        queryWrapper.eq(StringUtils.hasText(entity.getDistrict()), Community::getDistrict, entity.getDistrict());
        Community one = this.getOne(queryWrapper);

        if (one != null) {
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
        queryWrapper.like(StringUtils.hasText(searchDTO.getAddress()), Community::getAddress, searchDTO.getAddress())
                .like(StringUtils.hasText(searchDTO.getProjectName()), Community::getProjectName, searchDTO.getProjectName())
                .eq(StringUtils.hasText(searchDTO.getDistrict()), Community::getDistrict, searchDTO.getDistrict())
                .eq(searchDTO.getId() != null, Community::getId, searchDTO.getId())
                .eq(StringUtils.hasText(searchDTO.getProjectType()), Community::getProjectType, searchDTO.getProjectType());

        return this.page(page, queryWrapper);
    }
}
