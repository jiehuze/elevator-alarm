package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.KnowledgeMapper;
import com.schedule.elevator.dto.KnowledgeQueryDTO;
import com.schedule.elevator.entity.Knowledge;
import com.schedule.elevator.service.IKnowledgeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge>
        implements IKnowledgeService {

    @Override
    public Page<Knowledge> queryByConditions(KnowledgeQueryDTO dto) {
        int current = Math.max(1, dto.getPageNum());
        int size = Math.min(100, Math.max(1, dto.getPageSize()));

        Page<Knowledge> page = new Page<>(current, size);
        LambdaQueryWrapper<Knowledge> query = new LambdaQueryWrapper<>();

        // 按类型精确匹配
        if (StringUtils.isNotBlank(dto.getType())) {
            query.eq(Knowledge::getType, dto.getType());
        }

        // 关键词模糊搜索（标题或正文）
        if (StringUtils.isNotBlank(dto.getKeyword())) {
            query.like(Knowledge::getTitle, dto.getKeyword())
                    .or()
                    .like(Knowledge::getContent, dto.getKeyword());
        }

        // 按创建时间倒序
        query.orderByDesc(Knowledge::getCreateTime);

        return this.page(page, query);
    }
}