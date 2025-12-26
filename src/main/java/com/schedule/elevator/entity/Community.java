package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 小区信息表（含原始地址与结构化地址）
 */
@Data
@Accessors(chain = true)
@TableName("communities")
public class Community implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id; // 序号

    @TableField("address")
    private String address; // 完整小区地址（原始文本）

    @TableField("province")
    private String province; // 省份

    @TableField("city")
    private String city; // 城市

    @TableField("district")
    private String district; // 区/县

    @TableField("project_name")
    private String projectName; // 小区或场所名称

    /**
     * 小区所属地产品牌
     */
    @TableField("real_estate_brand")
    private String realEstateBrand;

    @TableField("project_type")
    private String projectType; // 类型（如：办公楼、住宅区、医院、交通场所、宾馆饭店等）

    @TableField(value = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @TableField(value = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

    @TableField(exist = false)
    private Long count;  // 计数属性，不映射到数据库
}
