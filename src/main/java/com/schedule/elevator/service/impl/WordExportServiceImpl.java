package com.schedule.elevator.service.impl;

import com.schedule.elevator.dto.*;
import com.schedule.elevator.entity.SysDistrict;
import com.schedule.elevator.service.*;
import com.schedule.excel.DocxPlaceholderReplaceUtil;
import com.schedule.excel.TableData;
import com.schedule.excel.WordExporter;
import com.schedule.utils.FileReplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordExportServiceImpl implements IWordExportService {
    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @Autowired
    private IFaultRecordService faultRecordService;

    @Autowired
    private ISysDistrictService sysDistrictService;

    @Override
    /**
     * 将表格数据直接写入到指定的 .docx 文件中
     *
     * @param title       表格标题（可为 null）
     * @param headers     表头列表（按列顺序）
     * @param dataRows    数据行列表（每行是字符串列表）
     * @param outputPath  输出文件路径，例如 "/tmp/电梯明细.docx"
     * @throws IOException 写入文件时可能抛出
     */
    public void generateWordTableToFile(
            String title,
            List<String> headers,
            List<List<String>> dataRows,
            String outputPath) throws IOException {

        WordExporter.generateWordTableToFile(title, headers, dataRows, outputPath);
    }

    /**
     * 基于模板，将多个占位符替换为对应的表格
     *
     * @param templatePath 模板路径（支持 classpath: 或 文件系统路径）
     * @param tableMap     占位符 -> 表格数据的映射，如 {"${elevator_table}", tableData}
     * @param outputPath   输出文件路径
     */
    public void generateWordFromTemplateWithMultipleTables(
            String templatePath,
            Map<String, TableData> tableMap,
            String outputPath) throws IOException {
        WordExporter.generateWordFromTemplateWithMultipleTables(templatePath, tableMap, outputPath);
    }

    /**
     * 基于模板，将多个占位符替换为对应的表格,导出月报
     *
     * @param searchDTO
     */
    @Override
    public void generateMonthlyReport(SearchDTO searchDTO, String outputPath) {
        try {
            String templatePath = "doc/month.docx";
//            String outputPath = "month.docx";

            HashMap<String, String> replaceStrMap = new HashMap<>();
            String districtS = "";
            if (searchDTO.getDistrict() == null) {
                List<SysDistrict> districtList = sysDistrictService.list();
                for (SysDistrict district : districtList) {
                    districtS += district.getDistrictName() + ",";
                }
            } else {
                districtS = searchDTO.getDistrict();
            }
            replaceStrMap.put("DistrictS", districtS);
            replaceStrMap.put("StartTime", searchDTO.getCreateTimeStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            replaceStrMap.put("EndTime", searchDTO.getCreateTimeEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            List<FaultResultDTO> faultResultDTOS = faultRecordService.statisticalFault(searchDTO);
            Long total = 0l;
            for (FaultResultDTO faultResultDTO : faultResultDTOS) {
                total += faultResultDTO.getTotals();
                replaceStrMap.put(faultResultDTO.getFaultCode(), faultResultDTO.getPercent().toString());
                for (FaultResultDTO child : faultResultDTO.getChild()) {
                    replaceStrMap.put(child.getFaultCode(), child.getTotals().toString());
                    replaceStrMap.put(child.getFaultCode() + "1", child.getPercent().toString());
                }
            }
            replaceStrMap.put("total", total.toString());

            InputStream in = new ClassPathResource(templatePath).getInputStream();
            InputStream inputStream = DocxPlaceholderReplaceUtil.replacePlaceholderToStream(in, replaceStrMap);

            List<ElevatorTypeStatsDTO> elevatorTypeStatsList = elevatorInfoService.statsByElevatorType(searchDTO);
            TableData elevatorTypeStatsTableData = ElevatorTypeStatsDTO.buildTableData(elevatorTypeStatsList);

            List<DistrictStatisticsDTO> districtStatistics = workOrderService.getDistrictStatistics(searchDTO);

            WorkOrderStatisticsDTO result = workOrderService.getWorkOrderStatisticsByCondition(searchDTO);

            List<SecondaryFaultStatsDTO> ordersByDuplicateRescueCode = workOrderService.getOrdersByDuplicateRescueCode(searchDTO);

//
            List<TimeSlotStatsDTO> stats = workOrderService.getFaultStatsByTimeSlot(searchDTO);

            List<TimeConsumptionStatsDTO> timeConsumptionStats = workOrderService.getTimeConsumptionStats(searchDTO);

            List<OvertimeWorkOrderDTO> overtimeWorkOrders = workOrderService.getOvertimeWorkOrders(searchDTO);

            ProjectTypeStatItemDTO projectTypeStats = workOrderService.getProjectTypeStats(searchDTO);
            TableData projectTypeTableData = ProjectTypeStatItemDTO.buildTableData(projectTypeStats);

            // 构建映射
            Map<String, TableData> tableMap = new HashMap<>();
            tableMap = TableData.buildTableData(tableMap, result, WorkOrderStatisticsDTO.class, "WorkOrderStatistics");
            tableMap = TableData.buildTableData(tableMap, districtStatistics, DistrictStatisticsDTO.class, "DistrictFault");
            tableMap = TableData.buildTableData(tableMap, stats, TimeSlotStatsDTO.class, "TimeSlotStats");
            tableMap = TableData.buildTableData(tableMap, timeConsumptionStats, TimeConsumptionStatsDTO.class, "TimeConsumptionStats");
            tableMap = TableData.buildTableData(tableMap, overtimeWorkOrders, OvertimeWorkOrderDTO.class, "OvertimeWorkOrder");
            tableMap = TableData.buildTableData(tableMap, ordersByDuplicateRescueCode, SecondaryFaultStatsDTO.class, "SecondaryFault");
            tableMap.put("ProjectTypeStats", projectTypeTableData);
            tableMap.put("ElevatorTypeStats", elevatorTypeStatsTableData);

            WordExporter.generateWordFromTemplateStreamWithMultipleTables(inputStream, tableMap, outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
