package com.schedule.elevator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MediaTokenData {
    // 权限范围（返回结果中scope为空）
    @JsonProperty("scope")
    private String scope;

    // 访问令牌
    @JsonProperty("access_token")
    private String accessToken;

    // 刷新令牌
    @JsonProperty("refresh_token")
    private String refreshToken;

    // 令牌类型（固定为bearer）
    @JsonProperty("token_type")
    private String tokenType;

    // 令牌过期时间（单位：秒）
    @JsonProperty("expires_in")
    private Integer expiresIn;
}
