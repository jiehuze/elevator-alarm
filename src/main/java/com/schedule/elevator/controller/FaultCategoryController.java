package com.schedule.elevator.controller;

import com.schedule.common.BaseResponse;
import com.schedule.elevator.entity.FaultCategory;
import com.schedule.elevator.service.IFaultCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 故障分类
 */
@RestController
@RequestMapping("/fault-category")
@RequiredArgsConstructor
public class FaultCategoryController {

    //    @Autowired
    private final IFaultCategoryService faultCategoryService;

    /**
     * 新增故障分类
     */
    @PostMapping("/add")
    public BaseResponse create(@RequestBody FaultCategory category) {
        faultCategoryService.save(category);

        return new BaseResponse(200, "success", null, null);
    }

    /**
     * 根据 ID 查询
     */
    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        FaultCategory faultCategory = faultCategoryService.getById(id);
        return new BaseResponse(200, "success", faultCategory, null);
    }

    /**
     * 修改故障分类（需传 id）
     */
    @PutMapping("/update")
    public BaseResponse update(@RequestBody FaultCategory category) {
        boolean update = faultCategoryService.updateById(category);

        return new BaseResponse(200, "success", update, null);
    }

    /**
     * 删除（物理删除）
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        faultCategoryService.removeById(id);
    }

    /**
     * 获取扁平列表（所有分类）
     */
    @GetMapping("/list")
    public BaseResponse listAll() {
        List<FaultCategory> list = faultCategoryService.list();

        return new BaseResponse(200, "success", list, null);
    }

    /**
     * 获取树形结构（用于前端选择器）
     */
    @GetMapping("/tree")
    public BaseResponse getTree() {
        List<FaultCategory> faultCategoryTree = faultCategoryService.getFaultCategoryTree();

        return new BaseResponse(200, "success", faultCategoryTree, null);
    }
}