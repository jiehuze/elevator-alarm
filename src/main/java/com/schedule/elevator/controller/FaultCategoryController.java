package com.schedule.elevator.controller;

import com.schedule.elevator.entity.FaultCategory;
import com.schedule.elevator.service.IFaultCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fault-category")
public class FaultCategoryController {

    @Autowired
    private IFaultCategoryService faultCategoryService;

    /**
     * 新增故障分类
     */
    @PostMapping("/add")
    public FaultCategory create(@RequestBody FaultCategory category) {
        faultCategoryService.save(category);
        return category;
    }

    /**
     * 根据 ID 查询
     */
    @GetMapping("/{id}")
    public FaultCategory getById(@PathVariable Long id) {
        return faultCategoryService.getById(id);
    }

    /**
     * 修改故障分类（需传 id）
     */
    @PutMapping("/update")
    public void update(@RequestBody FaultCategory category) {
        faultCategoryService.updateById(category);
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
    public List<FaultCategory> listAll() {
        return faultCategoryService.list();
    }

    /**
     * 获取树形结构（用于前端选择器）
     */
    @GetMapping("/tree")
    public List<FaultCategory> getTree() {
        return faultCategoryService.getFaultCategoryTree();
    }
}