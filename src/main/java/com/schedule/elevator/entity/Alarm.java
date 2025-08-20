package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("alarm")
public class Alarm {
    @TableId(type = IdType.AUTO)
    private Integer id; // 使用 Integer 而不是 Long
    private String name; // 名称
    private String type; // 类型
    private Integer typeId; // 类型id
    private Integer fillCycle; //通报周期
    private Integer actualGear; // 档位
    private Integer predictedGear; //预估档位
    private String creator; // 创建人
    private String creatorId; // 创建人id
    private Integer dateType; // 新增的日期类型字段
    private Integer year; // 年份
    private Integer quarter; // 季度
    private String leadingDepartment; // 牵头单位 'leading_department'
    private String leadingDepartmentId; // 牵头单位ID 'leading_department_id'
    private String responsibleDept; // 责任部门
    private String responsibleDeptId; // 责任部门id
    private String operator;
    private String operatorId;
    private String assigner; //交办人
    private String assignerId; //交办人Id
    private String checkStatus; //审核状态 1: 任务审核；2：阶段性审核；3：报表审核；4：指标审核，用逗号分割
    private String processInstanceId; //审核流水号
    private String processInstanceReviewIds; //审核的人，使用逗号分割
    private String processInstanceTargetReviewIds; //指标审核的人，使用逗号分割
    private java.sql.Timestamp createdAt; // 创建时间
    private java.sql.Timestamp updatedAt; // 更新时间
}