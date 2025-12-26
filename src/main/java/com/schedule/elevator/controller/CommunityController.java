package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.entity.Community;
import com.schedule.elevator.service.ICommunityService;
import com.schedule.elevator.service.IElevatorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private ICommunityService communityService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    /**
     * 添加社区信息
     */
    @PostMapping("/add")
    public BaseResponse create(@RequestBody Community community) {
        communityService.save(community);
        return new BaseResponse(HttpStatus.OK.value(), "添加成功", community, null);
    }

    /**
     * 删除社区信息
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        communityService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", null, null);
    }

    /**
     * 更新社区信息
     */
    @PutMapping("/update")
    public BaseResponse update(@RequestBody Community community) {
        communityService.updateById(community);
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", community, null);
    }

    /**
     * 根据ID获取社区信息
     */
    @GetMapping("/{id}")
    public BaseResponse get(@PathVariable Long id) {
        Community community = communityService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", community, null);
    }

    /**
     * 分页查询社区列表
     */
    @GetMapping("/list")
    public BaseResponse list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute Community searchInfo) {

        Page<Community> page = new Page<>(current, size);
        IPage<Community> result = communityService.pageCommunities(page, searchInfo);
        if (result != null) {
            for (Community community : result.getRecords()) {
                ElevatorInfoDTO elevatorInfoDTO = new ElevatorInfoDTO();
                elevatorInfoDTO.setCommunityId(community.getId());

                community.setCount(elevatorInfoService.count(elevatorInfoDTO));
            }
        }
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }

    /**
     * 获取所有社区列表
     */
    @GetMapping("/all")
    public BaseResponse getAll() {
        List<Community> communities = communityService.list();
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", communities, null);
    }
}
