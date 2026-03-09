package com.schedule.elevator.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ParamDTO {
    @Value("${elevator.root.path}")
    private String rootPath;
    @Value("${elevator.screenshot.path}")
    private String screenshotPath;
    @Value("${elevator.report.path}")
    private String reportPath;
    @Value("${elevator.export.path}")
    private String exportPath;
    @Value("${elevator.maintenance.path}")
    private String maintenancePath;
    @Value("${elevator.media.url}")
    private String mediaUrl;
}
