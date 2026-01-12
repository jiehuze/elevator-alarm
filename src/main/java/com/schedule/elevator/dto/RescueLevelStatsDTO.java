package com.schedule.elevator.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class RescueLevelStatsDTO implements Serializable {
    private Integer level1; // 1级救援数量
    private Integer level2; // 2级救援数量
    private Integer level3; // 3级救援数量
    private Integer total;  // 总数
}