package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.PropertyInfoDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.entity.PropertyInfo;
import com.schedule.elevator.entity.SafetyOfficer;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IPropertyInfoService;
import com.schedule.elevator.service.ISafetyOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 使用单位接口
 */
@RestController
@RequestMapping("/property")
public class PropertyInfoController {

    @Autowired
    private IPropertyInfoService IPropertyInfoService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @Autowired
    private ISafetyOfficerService safetyOfficerService;

    /**
     * 根据 ID 查询
     */
    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        PropertyInfo info = IPropertyInfoService.getById(id);

        return new BaseResponse(HttpStatus.OK.value(), "success", info, null);
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        long count = elevatorInfoService.count(new LambdaQueryWrapper<ElevatorInfo>().eq(ElevatorInfo::getUsingUnitId, id));
        if (count > 0) {
            return new BaseResponse(HttpStatus.FORBIDDEN.value(), "该使用单位已绑定电梯，请先解除绑定", null, null);
        }
        long safetyOfficerCount = safetyOfficerService.count(new LambdaQueryWrapper<SafetyOfficer>().eq(SafetyOfficer::getUsingUnitId, id));
        if (safetyOfficerCount > 0) {
            return new BaseResponse(HttpStatus.FORBIDDEN.value(), "该使用单位已绑定安全员，请先解除绑定", null, null);
        }
        boolean deleted = IPropertyInfoService.removeById(id);
        return new BaseResponse(deleted ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value(),
                deleted ? "删除成功" : "删除失败", null, null);
    }

    @GetMapping("/list")
    public BaseResponse listAll(@ModelAttribute PropertyInfoDTO dto) {
        System.out.println("--------------" + dto);
        Page<PropertyInfo> list = IPropertyInfoService.queryByConditionsPage(dto);
        if (list != null) {
            for (PropertyInfo info : list.getRecords()) {
                ElevatorInfoDTO elevatorInfoDTO = new ElevatorInfoDTO();
                elevatorInfoDTO.setUsingUnitId(info.getId());
                info.setCount(elevatorInfoService.count(elevatorInfoDTO));
                info.setSafetyOfficerCount(safetyOfficerService.count(new LambdaQueryWrapper<SafetyOfficer>().eq(SafetyOfficer::getUsingUnitId, info.getId())));
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", list, null);
    }

    /**
     * 根据 unitCode 查询
     */
    @GetMapping("/code/{unitCode}")
    public BaseResponse getByUnitCode(@PathVariable String unitCode) {
        PropertyInfo info = IPropertyInfoService.getByUnitCode(unitCode);
        if (info == null) {
            return new BaseResponse(HttpStatus.NOT_FOUND.value(), "未找到使用单位", null, null);
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", info, null);
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public BaseResponse add(@RequestBody PropertyInfo entity) {
        boolean saved = IPropertyInfoService.save(entity);
        return new BaseResponse(HttpStatus.OK.value(), "添加", saved, null);
    }

    /**
     * 更新（按 ID）
     */
    @PutMapping("/update")
    public BaseResponse update(@RequestBody PropertyInfo entity) {
        boolean updated = IPropertyInfoService.updateById(entity);
        if (entity.getId() != null && entity.getUsingUnit() != null) {
            ElevatorInfo elevatorInfo = new ElevatorInfo();
            elevatorInfo.setUsingUnit(entity.getUsingUnit());
            elevatorInfoService.update(elevatorInfo, new LambdaQueryWrapper<ElevatorInfo>().eq(ElevatorInfo::getUsingUnitId, entity.getId()));
        }

        return new BaseResponse(updated ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value(),
                updated ? "更新成功" : "更新失败", entity, null);
    }

    /**
     * 新增或更新（按 unitCode）
     */
    @PostMapping("/upsert")
    public BaseResponse upsert(@RequestBody PropertyInfo entity) {
        boolean ok = IPropertyInfoService.saveOrUpdateByUnitCode(entity);
        return new BaseResponse(ok ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ok ? "保存成功" : "保存失败", entity, null);
    }
}
