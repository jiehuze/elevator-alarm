package com.schedule.elevator.dto;

import com.schedule.elevator.entity.PropertyInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("使用单位查询条件")
public class PropertyInfoDTO extends PropertyInfo implements Serializable {
    @ApiModelProperty("页码，默认1")
    private Integer current = 1;

    @ApiModelProperty("每页大小，默认10")
    private Integer size = 10;
}
