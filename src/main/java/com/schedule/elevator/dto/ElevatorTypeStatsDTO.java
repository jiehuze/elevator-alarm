package com.schedule.elevator.dto;

import com.schedule.excel.TableData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ElevatorTypeStatsDTO {
    private String elevatorType;
    private Integer elevatorCount;

    public static TableData buildTableData(List<ElevatorTypeStatsDTO> dtos) {
        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        ArrayList<String> firstRows = new ArrayList<>();

        Integer total = 0;
        headers.add("合计");
        firstRows.add(total.toString());

        for (ElevatorTypeStatsDTO dto : dtos) {
            headers.add(dto.getElevatorType());
            if (dto.getElevatorCount() == null) {
                dto.setElevatorCount(0);
            }
            firstRows.add(dto.getElevatorCount().toString());
            total += dto.getElevatorCount();
        }
        firstRows.set(0, total.toString());
        rows.add(firstRows);

        return new TableData(headers, rows);
    }
}
