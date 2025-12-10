package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.UserTokenMapper;
import com.schedule.elevator.entity.UserToken;
import com.schedule.elevator.service.IUserTokenService;
import com.schedule.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserTokenServiceImpl extends ServiceImpl<UserTokenMapper, UserToken>
        implements IUserTokenService {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserToken createToken(Long userId) {
        // 1. 生成 JWT
        String jwtToken = jwtUtil.generateToken(userId);
        Claims claims = jwtUtil.parseClaims(jwtToken);
        String jti = claims.getId(); // 使用 jti 作为数据库 token 字段值
        // 2. 注销该用户所有旧 Token（状态设为 0）
        this.update(
                new LambdaUpdateWrapper<UserToken>()
                        .set(UserToken::getStatus, 0)
                        .eq(UserToken::getUserId, userId)
        );

        // 3. 保存新 Token 记录（只存 jti）
        UserToken userToken = new UserToken();
        userToken.setUserId(userId);
        userToken.setToken(jti);
        userToken.setExpiresAt(LocalDateTime.ofInstant(
                claims.getExpiration().toInstant(),
                java.time.ZoneId.systemDefault()
        ));
        userToken.setStatus(1); // 1 = 有效

        this.save(userToken);

        return userToken;
    }

    @Override
    public boolean isTokenValidForUser(Long userId, String jti) {
        UserToken token = this.getOne(new LambdaQueryWrapper<UserToken>()
                .eq(UserToken::getUserId, userId)
                .eq(UserToken::getToken, jti)
                .eq(UserToken::getStatus, 1)
                .gt(UserToken::getExpiresAt, LocalDateTime.now())
        );
        return token != null;
    }

    @Override
    public boolean validateToken(String jwtToken) {
        try {
            Claims claims = jwtUtil.parseClaims(jwtToken);
            if (jwtUtil.isTokenExpired(claims)) {
                return false;
            }
            String jti = claims.getId();
            Long userId = Long.valueOf(claims.getSubject());

            return isTokenValidForUser(userId, jti);
        } catch (Exception e) {
            // JWT 解析失败、格式错误、签名无效等
            return false;
        }
    }

    // UserTokenServiceImpl.java

    @Override
    public boolean logout(String jwtToken) {
        try {
            Claims claims = jwtUtil.parseClaims(jwtToken);
            String jti = claims.getId();
            Long userId = Long.valueOf(claims.getSubject());

            // 将该用户的这个 token 标记为已注销
            boolean updated = this.update(
                    new LambdaUpdateWrapper<UserToken>()
                            .set(UserToken::getStatus, 0)
                            .eq(UserToken::getUserId, userId)
                            .eq(UserToken::getToken, jti)
                            .eq(UserToken::getStatus, 1) // 只更新“有效”的 token
            );

            return updated;
        } catch (Exception e) {
            // JWT 无效、已过期、格式错误等，视为登出成功（反正也无法使用）
            return true;
        }
    }

    @Override
    public Claims parseClaims(String token) {
        return jwtUtil.parseClaims(token);
    }
}
