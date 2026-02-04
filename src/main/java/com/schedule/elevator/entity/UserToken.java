package com.schedule.elevator.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_token")
public class UserToken {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId; // 存储用户 ID

//    private String username;

//    private String employeeId;           // 报警员工id

//    private String roles; // 存储用户角色

    private String token; // 存储 JWT 的 jti（唯一ID）

    private LocalDateTime expiresAt; // 存储 JWT 的过期时间

    private Integer status; // 1: 有效, 0: 已注销

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
