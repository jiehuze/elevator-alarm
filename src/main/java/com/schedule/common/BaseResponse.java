package com.schedule.common;

import lombok.Data;

@Data
public class BaseResponse {
    Integer code;
    String msg;
    Object data;
    String err;

    public BaseResponse(Integer code, String msg, Object data, String err) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.err = err;
    }
}
