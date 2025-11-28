package com.schedule.elevator.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ParamDTO {
    @Value("${elevator.alarm.upload.path}")
    private String uploadPath;
    @Value("${elevator.alarm.download.path}")
    private String downloadPath;
}
