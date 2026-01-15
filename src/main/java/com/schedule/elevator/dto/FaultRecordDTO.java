package com.schedule.elevator.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class FaultRecordDTO implements Serializable {
    private String faultRootCode;
    private String faultSubCode;
    private String faultRootDescription;
    private String faultSubDescription;
    private String faultDescription;
    private String faultTime;
}
