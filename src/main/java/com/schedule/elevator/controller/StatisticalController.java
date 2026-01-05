package com.schedule.elevator.controller;

import com.schedule.common.BaseResponse;
import com.schedule.elevator.service.IWorkOrderProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistical")
public class StatisticalController {

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    @GetMapping("/list")
    public BaseResponse list() {
        return new BaseResponse(HttpStatus.OK.value(), "success", workOrderProgressService.list(), null);
    }
}
