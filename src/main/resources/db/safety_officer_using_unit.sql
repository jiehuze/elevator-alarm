-- 安全员与使用单位对应关系表
CREATE TABLE `safety_officer_using_unit` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `safety_officer_id` BIGINT NOT NULL COMMENT '安全员ID',
    `using_unit_id` BIGINT NOT NULL COMMENT '使用单位ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_safety_officer_using_unit` (`safety_officer_id`, `using_unit_id`) COMMENT '安全员与使用单位唯一对应关系',
    KEY `idx_safety_officer_id` (`safety_officer_id`) COMMENT '安全员ID索引',
    KEY `idx_using_unit_id` (`using_unit_id`) COMMENT '使用单位ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全员与使用单位对应关系表';
