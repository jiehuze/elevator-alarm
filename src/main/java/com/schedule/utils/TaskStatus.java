package com.schedule.utils;

/**
 * 任务状态枚举
 */
public enum TaskStatus {
    TASKSTATUS_RECEIVED(1, "待接收"),
    TASKSTATUS_NORMAL_PROGRESS(2, "正常推进"),
    TASKSTATUS_OVERDUE(3, "已超期"),
    TASKSTATUS_REVIEW_LEADER_CB(4, "审核中（承办领导）"),
    TASKSTATUS_REVIEW_LEADER_QT(10, "审核中（牵头领导）"),
    TASKSTATUS_REVIEW_ASSIGNER(5, "审核中（交办人）"),
    TASKSTATUS_COMPLETED(6, "已完成"),
    TASKSTATUS_CANCEL_REVIEW_LEADER_CB(7, "取消审核中（承办领导）"),
    TASKSTATUS_CANCEL_REVIEW_LEADER_QT(11, "取消审核中（牵头领导）"),
    TASKSTATUS_CANCEL_REVIEW_ASSIGNER(8, "取消审核中（交办人）"),
    TASKSTATUS_CANCELLED(9, "已取消"),

    TASKSTATUS_REPORT(100, "填报审核中（承包领导）");

    private final Integer code; // 状态码
    private final String description; // 状态描述

    TaskStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取枚举实例
     */
    public static TaskStatus getByCode(Integer code) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的状态码: " + code);
    }
}