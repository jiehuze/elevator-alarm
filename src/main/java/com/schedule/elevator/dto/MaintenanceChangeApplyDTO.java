package com.schedule.elevator.dto;

import com.schedule.elevator.entity.ElevatorInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 维保单位变更申请内容DTO
 */
@Data
@Accessors(chain = true)
public class MaintenanceChangeApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 电梯列表
     */
    private List<ElevatorInfo> elevatorList;

    /**
     * 维保类型
     */
    private String maintenanceType;
    /**
     * 维保单位ID
     */
    private Long maintenanceUnitId;

    /**
     * 维保单位名称
     */
    private String maintenanceUnit;

    /**
     * 维保班组ID
     */
    private Long maintenanceTeamId;

    /**
     * 维保人员ID
     */
    private Long maintenancePersonnelId;

    /**
     * 维保人员姓名
     */
    private String maintenancePersonnelName;

    /**
     * 维保人员电话
     */
    private String maintenancePersonnelPhone;

    /**
     * 维保单位是否脱保：0-否，1-是
     */
    private Boolean maintenanceUnitChanged;

    /**
     * 照片URL
     */
    private String photoUrl;
}
