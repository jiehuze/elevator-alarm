package com.schedule.elevator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 接口通用返回结果
 */
@Data
@Accessors(chain = true)
public class ApiResponse {
    // 状态码：0成功，非0失败
    @JsonProperty("code")
    private Integer code;
    // 响应数据
    @JsonProperty("data")
    private Object data;
    // 响应信息
    @JsonProperty("msg")
    private String msg;
}
