package com.schedule.elevator.controller;

import com.schedule.common.BaseResponse;
import com.schedule.elevator.entity.SysUser;
import com.schedule.elevator.entity.UserToken;
import com.schedule.elevator.service.ISysUserService;
import com.schedule.elevator.service.IUserTokenService;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IUserTokenService userTokenService;

    /**
     * 注册用户
     */
    @PostMapping("/register")
    public BaseResponse register(@RequestBody SysUser user) {
        SysUser register = sysUserService.register(user);

        return new BaseResponse(HttpStatus.OK.value(), "注册成功", register, null);
    }

    /**
     * 根据 ID 查询用户
     */
    @GetMapping("/{id}")
    public BaseResponse getUserById(@PathVariable Long id) {
        SysUser sysUser = sysUserService.getById(id);

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", sysUser, null);
    }

    public BaseResponse updateUser(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", user, null);
    }

    /**
     * 示例：登录逻辑（仅查用户，验证密码需在 Security 中处理）
     */
    @PostMapping("/login")
    public BaseResponse login(@RequestBody SysUser sysUser) {
        SysUser auth = sysUserService.auth(sysUser);
        if (auth == null) {
            return new BaseResponse(HttpStatus.UNAUTHORIZED.value(), "账号或者密码错误", null, null);
        }
        //生成token
        UserToken userToken = userTokenService.createToken(auth.getId());

        return new BaseResponse(HttpStatus.OK.value(), "登录成功", userToken, null);
    }

    @GetMapping("/logout")
    public BaseResponse logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Boolean logout = userTokenService.logout(token);
            return new BaseResponse(HttpStatus.OK.value(), "登出成功", logout, null);
        }

        return new BaseResponse(HttpStatus.OK.value(), "登出失败", null, null);
    }
}
