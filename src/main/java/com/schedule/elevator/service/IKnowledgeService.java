package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.KnowledgeQueryDTO;
import com.schedule.elevator.entity.Knowledge;

public interface IKnowledgeService extends IService<Knowledge> {

    Page<Knowledge> queryByConditions(KnowledgeQueryDTO dto);
}