package com.schedule.elevator.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class FaultResultDTO implements Serializable {
    private String faultCode;
    private String faultName;
    private Long totals;
    private BigDecimal percent;
    private List<FaultResultDTO> child;
}
