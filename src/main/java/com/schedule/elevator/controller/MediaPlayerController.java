package com.schedule.elevator.controller;

import com.schedule.common.BaseResponse;
import com.schedule.elevator.service.IMediaPlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/play")
public class MediaPlayerController {

    @Autowired
    private IMediaPlayService mediaPlayService;

    @GetMapping("/{id}")
    public BaseResponse getUserById(@PathVariable String id) {
        Object playUrl = mediaPlayService.getPlayUrl(id);
        if (playUrl == null) {
            String code = mediaPlayService.getCode();
            if (code == null) {
                mediaPlayService.getToken(code);
                playUrl = mediaPlayService.getPlayUrl(id);
                if (playUrl == null) {
                    return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取播放地址错误。", null, null);
                }
            } else {
                return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取播放地址错误。", null, null);
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", playUrl, null);
    }
}
