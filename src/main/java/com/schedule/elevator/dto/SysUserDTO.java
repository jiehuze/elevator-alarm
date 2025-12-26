package com.schedule.elevator.dto;

import com.schedule.elevator.entity.SysUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysUserDTO extends SysUser implements Serializable {
    @ApiModelProperty("页码，默认1")
    private Integer current = 1;

    @ApiModelProperty("每页大小，默认10")
    private Integer size = 10;
}
