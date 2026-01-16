package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.FaultCategoryMapper;
import com.schedule.elevator.entity.FaultCategory;
import com.schedule.elevator.service.IFaultCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FaultCategoryServiceImpl extends ServiceImpl<FaultCategoryMapper, FaultCategory>
        implements IFaultCategoryService {

//    @Override
//    public List<FaultCategory> getFaultCategoryTree2() {
//        // 使用 MyBatis-Plus LambdaQuery 查询所有数据，并按 level 和 id 升序排列
//        List<FaultCategory> allCategories = this.lambdaQuery()
//                .orderByAsc(FaultCategory::getLevel)
//                .orderByAsc(FaultCategory::getId)
//                .list();
//
//        Map<Long, FaultCategory> nodeMap = allCategories.stream()
//                .collect(Collectors.toMap(FaultCategory::getId, c -> c));
//
//        System.out.println(nodeMap);
//
//        nodeMap.values().forEach(node -> node.setChildren(new ArrayList<>()));
//
//        List<FaultCategory> roots = new ArrayList<>();
//        for (FaultCategory node : allCategories) {
//            if (node.getParentId() == 0) {
//                roots.add(node);
//            } else {
//                FaultCategory parent = nodeMap.get(node.getParentId());
//                if (parent != null) {
//                    parent.getChildren().add(node);
//                }
//            }
//        }
//
//        return roots;
//    }

    @Override
    public List<FaultCategory> getFaultCategoryTree() {
        // 使用 MyBatis-Plus LambdaQuery 查询所有数据，并按 level 和 id 升序排列
        List<FaultCategory> allCategories = this.lambdaQuery()
                .orderByAsc(FaultCategory::getLevel)
                .orderByAsc(FaultCategory::getId)
                .list();

        // 使用 code 字段作为 map 的 key
        Map<String, FaultCategory> nodeMap = allCategories.stream()
                .collect(Collectors.toMap(FaultCategory::getFaultCode, c -> c));

        System.out.println(nodeMap);

        // 初始化所有节点的 children 列表
        nodeMap.values().forEach(node -> node.setChildren(new ArrayList<>()));

        List<FaultCategory> roots = new ArrayList<>();
        for (FaultCategory node : allCategories) {
            // 检查是否为根节点（parentCode 为空或为顶级标识）
            if (node.getParentCode() == null || node.getParentCode().isEmpty() || "0".equals(node.getParentCode())) {
                roots.add(node);
            } else {
                // 通过 parentCode 查找父节点
                FaultCategory parent = nodeMap.get(node.getParentCode());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return roots;
    }
}