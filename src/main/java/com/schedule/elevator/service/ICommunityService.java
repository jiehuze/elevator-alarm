package com.schedule.elevator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.Community;
import com.schedule.elevator.entity.PropertyInfo;

import java.util.List;

public interface ICommunityService extends IService<Community> {
    // 创建小区或者项目信息
    long getOrCreateCommunityId(Community entity);

    /**
     * 分页查询社区信息
     */
    IPage<Community> pageCommunities(Page<Community> page, Community searchDTO);

    List<Community> listCommunities(SearchDTO searchDTO);
}
