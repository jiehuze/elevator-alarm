package com.schedule.elevator.service;

import com.schedule.elevator.dto.MediaTokenData;

public interface IMediaPlayService {
    // 获取播放代码
    String getCode();

    // 获取登录token
    MediaTokenData getToken(String code);

    // 获取播放地址
    Object getPlayUrl(String videoId);
}
