package com.schedule.elevator.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel("处理进度查询条件")
public class HandleProgressDTO implements Serializable {
}
