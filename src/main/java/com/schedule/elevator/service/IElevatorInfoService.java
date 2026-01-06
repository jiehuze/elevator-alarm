package com.schedule.elevator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.ElevatorInfo;

import java.util.List;
import java.util.Map;

public interface IElevatorInfoService extends IService<ElevatorInfo> {

    IPage<ElevatorInfo> pageElevators(Page<ElevatorInfo> page, ElevatorInfoDTO elevatorInfoDTO);

    List<ElevatorInfo> listElevators(String keyword);

    boolean createElevatorInfo(ElevatorInfo elevatorInfo) throws Exception;

    /* *
        统计数据相关
     */
    Long count(ElevatorInfoDTO dto);

    /**
     * 按电梯类型统计数量（支持时间范围筛选）
     * 合计	曳引驱动乘客电梯	曳引驱动乘客货梯	强制驱动载货电梯	液压载货电梯	防爆电梯	自动扶梯	自动人行道	杂物电梯	曳引驱动观光电梯
     * 19892	18427	5	6	3	1	963	127	66	294
     */
    List<Map<String, Object>> countByElevatorType(SearchDTO searchDTO);
}