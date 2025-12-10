package com.schedule.elevator.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.entity.UserToken;
import io.jsonwebtoken.Claims;

public interface IUserTokenService extends IService<UserToken> {

    /**
     * 创建新 Token，并将该用户所有旧 Token 标记为已注销
     *
     * @param userId 用户ID
     * @return 完整的 JWT Token 字符串（返回给前端）
     */
    UserToken createToken(Long userId);

    /**
     * 查询该账号下指定 token (jti) 是否正常（有效、未过期、未注销）
     *
     * @param userId 用户ID
     * @param jti    JWT 的唯一标识（jti）
     * @return true if valid
     */
    boolean isTokenValidForUser(Long userId, String jti);

    /**
     * 校验完整 JWT Token 是否有效（用于拦截器/认证）
     *
     * @param jwtToken 完整的 JWT 字符串（如 "eyJhbGciOiJIUzI1NiIs..."）
     * @return true if valid
     */
    boolean validateToken(String jwtToken);

    /**
     * 注销当前 Token（根据完整 JWT 字符串）
     *
     * @param jwtToken 完整的 JWT（如 "Bearer xxx" 中的 xxx）
     * @return true if successfully logged out
     */
    boolean logout(String jwtToken);

    Claims parseClaims(String token);
}
