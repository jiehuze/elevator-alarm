package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.KnowledgeQueryDTO;
import com.schedule.elevator.entity.Knowledge;
import com.schedule.elevator.service.IKnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    @Autowired
    private IKnowledgeService knowledgeService;

    /**
     * 分页查询知识库文章
     */
    @GetMapping("/list")
    public BaseResponse list(KnowledgeQueryDTO dto) {
        Page<Knowledge> knowledgePage = knowledgeService.queryByConditions(dto);
        return new BaseResponse(HttpStatus.OK.value(), "success", knowledgePage, null);
    }

    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        Knowledge knowledge = knowledgeService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", knowledge, null);
    }

    /**
     * 新增知识库文章
     */
    @PostMapping("/add")
    public BaseResponse create(@RequestBody Knowledge knowledge) {
        knowledgeService.save(knowledge);
        return new BaseResponse(HttpStatus.OK.value(), "success", knowledge, null);
    }
}