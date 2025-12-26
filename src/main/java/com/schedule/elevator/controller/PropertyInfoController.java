package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.PropertyInfoDTO;
import com.schedule.elevator.entity.PropertyInfo;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IPropertyInfoService;
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
