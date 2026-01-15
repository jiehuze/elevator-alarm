package com.schedule.elevator.dto;

import com.schedule.excel.TableData;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectTypeStatItemDTO {
    private Long total;
    private Long faultTotal;
    private List<ProjectTypeCountDTO> projectTypeCounts;

    public static TableData buildTableData(ProjectTypeStatItemDTO dto) {
        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        ArrayList<String> firstRows = new ArrayList<>();
        ArrayList<String> secondRows = new ArrayList<>();
        headers.add("");
        firstRows.add("故障数");
        secondRows.add("电梯数");

        headers.add("合计");
        firstRows.add(dto.getFaultTotal().toString());
        secondRows.add(dto.getTotal().toString());

        for (ProjectTypeCountDTO item : dto.getProjectTypeCounts()) {
            headers.add(item.getProjectName());
            firstRows.add(item.getFaultCount().toString());
            secondRows.add(item.getCount().toString());
        }

        rows.add(firstRows);
        rows.add(secondRows);

        return new TableData(headers, rows);
    }
}
