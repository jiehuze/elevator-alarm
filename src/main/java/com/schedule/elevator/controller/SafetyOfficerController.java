package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.entity.SafetyOfficer;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.ISafetyOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/safety-officer")
public class SafetyOfficerController {

    @Autowired
    private ISafetyOfficerService safetyOfficerService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @PostMapping
    public BaseResponse create(@RequestBody SafetyOfficer safetyOfficer) {
        try {
            // 检查同一单位内电话是否重复
            if (safetyOfficerService.existsByUnitIdAndPhone(
                    safetyOfficer.getUsingUnitId(),
                    safetyOfficer.getSafetyOfficerPhone(),
                    null)) {
                return new BaseResponse(HttpStatus.OK.value(), "该单位内已存在相同电话的安全员", null, null);
            }

            boolean saved = safetyOfficerService.save(safetyOfficer);
            return new BaseResponse(HttpStatus.OK.value(), "创建成功", saved, null);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "创建失败", e.getMessage(), null);
        }
    }

    @GetMapping("/{id}")
    public SafetyOfficer getById(@PathVariable Long id) {
        return safetyOfficerService.getById(id);
    }

    @PutMapping("/{id}")
    public BaseResponse update(@RequestBody SafetyOfficer safetyOfficer, @PathVariable Long id) {
        boolean update = safetyOfficerService.update(safetyOfficer, new LambdaQueryWrapper<SafetyOfficer>().eq(SafetyOfficer::getId, id));
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", update, null);
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        boolean delete = safetyOfficerService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", delete, null);
    }

    @GetMapping("/list")
    public BaseResponse list(@ModelAttribute SearchDTO searchDTO) {
        Page<SafetyOfficer> safetyOfficerPage = safetyOfficerService.queryByConditionsPage(searchDTO);
        for (SafetyOfficer person : safetyOfficerPage.getRecords()) {
            long count = elevatorInfoService.count(new LambdaQueryWrapper<ElevatorInfo>().eq(ElevatorInfo::getSafetyOfficerId, person.getId()));
            person.setCount(count);
        }
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", safetyOfficerPage, null);
    }
}
