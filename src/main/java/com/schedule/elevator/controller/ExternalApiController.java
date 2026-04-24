package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.entity.OnekeyCall;
import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.entity.WorkOrderProgress;
import com.schedule.elevator.enums.WorkOrderStatusEnum;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IOnekeyCallService;
import com.schedule.elevator.service.IWorkOrderProgressService;
import com.schedule.elevator.service.IWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 外部API控制器
 * 提供给外部系统调用的接口，需要通过API Key + HMAC签名鉴权
 * <p>
 * 鉴权方式：
 * 1. 在请求头中传递 X-Api-Key、X-Timestamp、X-Signature
 * 2. 签名计算：HMAC-SHA256(timestamp + apiKey, secretKey)
 * 3. 时间戳有效期为5分钟
 */
@RestController
@RequestMapping("/external")
public class ExternalApiController {

    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @Autowired
    private IOnekeyCallService onekeyCallService;

    /**
     * 创建困人工单
     * 与内部创建工单流程一致：
     */
    @PostMapping("/trapped")
    public BaseResponse createTrappedOrder(@RequestBody OnekeyCall onekeyCall) {

        if (onekeyCall == null
                || onekeyCall.getRescueCode() == null
                || onekeyCall.getRescueCode().isEmpty()
                || onekeyCall.getDeviceCode() == null
                || onekeyCall.getCallNo() == null) {
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "参数错误", null, null);
        }
        ElevatorInfo elevatorInfo = elevatorInfoService.getOne(new LambdaQueryWrapper<ElevatorInfo>().eq(ElevatorInfo::getRescueCode, onekeyCall.getRescueCode()));
        if (elevatorInfo == null) {
            return new BaseResponse(HttpStatus.NOT_FOUND.value(), "设备不存在", null, null);
        }

        WorkOrder workOrder = new WorkOrder()
                .setOrderNo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .setStatus(0)
                .setOrderType(1)
                .setEmployeeId("000001")
                .setRescueCode(onekeyCall.getRescueCode())
                .setRegisterCode(elevatorInfo.getRegisterCode())
                .setIncidentDescription("一键呼")
                .setAlarmSource("一键呼")
                .setAlarmPersonName("一键呼")
                .setAlarmPersonPhone("96365")
                .setElevatorName(elevatorInfo.getElevatorName())
                .setElevatorAddress(elevatorInfo.getLocation())
                .setProjectName(elevatorInfo.getProjectName())
                .setProjectType(elevatorInfo.getProjectType())
                .setUsingUnit(elevatorInfo.getUsingUnit())
                .setUsingUnitId(elevatorInfo.getUsingUnitId())
                .setMaintenanceUnit(elevatorInfo.getMaintenanceUnit())
                .setMaintenanceUnitId(elevatorInfo.getMaintenanceUnitId())
                .setMaintenancePersonnelName(elevatorInfo.getMaintenancePersonnelName())
                .setMaintenancePersonnelId(elevatorInfo.getMaintenancePersonnelId())
                .setMaintenancePersonnelPhone(elevatorInfo.getMaintenancePersonnelPhone())
                .setMaintenanceTeamId(elevatorInfo.getMaintenanceTeamId())
//                .setMaintenanceTeamName(elevatorInfo.getMaintenanceTeamName())
                .setMaintenancePersonnelPhone(elevatorInfo.getMaintenancePersonnelPhone())
                .setRescueLevel(1)
                .setDistrict(elevatorInfo.getDistrict());

        WorkOrder wd = workOrderService.createWorkOrder(workOrder);
        //添加记录
        WorkOrderProgress workOrderProgress = new WorkOrderProgress().setOrderNo(wd.getOrderNo())
                .setProgress(WorkOrderStatusEnum.getByCode(workOrder.getStatus()).getDescription())
                .setResult("成功")
                .setStatus(wd.getStatus())
                .setRemark(wd.getIncidentDescription())
                .setEmployeeId(wd.getEmployeeId());
        workOrderProgressService.save(workOrderProgress);

        onekeyCall.setProjectName(elevatorInfo.getProjectName());
        onekeyCall.setDistrict(elevatorInfo.getDistrict());
        onekeyCall.setOrderNo(wd.getOrderNo());

        onekeyCallService.save(onekeyCall);

        return new BaseResponse(HttpStatus.OK.value(), "success", null, null);
    }
}
