package com.schedule.excel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 电梯导入校验结果
 */
@Data
public class ElevatorImportValidationResult {

    /**
     * 行号（从1开始，包含表头）
     */
    private Integer rowNum;

    /**
     * 电梯注册代码（用于标识）
     */
    private String registerCode;

    /**
     * 电梯救援码（用于标识）
     */
    private String rescueCode;

    /**
     * 是否校验通过
     */
    private boolean valid;

    /**
     * 错误信息列表
     */
    private List<String> errorMessages;

    /**
     * 原始数据
     */
    private ElevatorImportTemplateExcel originalData;

    public ElevatorImportValidationResult() {
        this.errorMessages = new ArrayList<>();
        this.valid = true;
    }

    public void addError(String errorMessage) {
        this.errorMessages.add(errorMessage);
        this.valid = false;
    }

    public String getErrorMessageString() {
        return String.join("; ", errorMessages);
    }
}
