package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.SysUserDTO;
import com.schedule.elevator.dto.UserTokenDTO;
import com.schedule.elevator.entity.MaintenanceUnit;
import com.schedule.elevator.entity.SysUser;
import com.schedule.elevator.entity.UserToken;
import com.schedule.elevator.service.IMaintenanceUnitService;
import com.schedule.elevator.service.ISysUserService;
import com.schedule.elevator.service.IUserTokenService;
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

    @Autowired
    private IMaintenanceUnitService maintenanceUnitService;

    /**
     * 注册用户
     */
    @PostMapping("/register")
    public BaseResponse register(@RequestBody SysUser user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getRoles() == null)
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "用户名和密码,角色不能为空", null, null);
        if (user.getRoles().contains("maintenance")) {
            if (user.getMaintenanceUnitId() == null) {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "维护单位用户需要选择维护单位", null, null);
            }
            MaintenanceUnit maintenanceUnit = maintenanceUnitService.getById(user.getMaintenanceUnitId());
            if (maintenanceUnit == null) {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "维护单位不存在", null, null);
            }
        }
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

    @PostMapping("/password")
    public BaseResponse updatePwd(@RequestBody SysUserDTO userDTO) {
        SysUser auth = sysUserService.auth(userDTO);
        if (auth == null) {
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "账号或者密码错误", null, null);
        }

        auth.setPassword(userDTO.getNewPassword());
        Boolean update = sysUserService.updateUser(auth);

        return new BaseResponse(HttpStatus.OK.value(), "更新成功", update, null);
    }

    @GetMapping("/list")
    public BaseResponse listAll(@ModelAttribute SysUserDTO query) {
        Page<SysUser> list = sysUserService.querySysUserPage(query);
        for (SysUser user : list.getRecords()) {
            if (user.getMaintenanceUnitId() != null) {
                MaintenanceUnit maintenanceUnit = maintenanceUnitService.getById(user.getMaintenanceUnitId());
                user.setMaintenanceUnit(maintenanceUnit.getMaintenanceUnit());
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", list, null);
    }

    @PostMapping("/update")
    public BaseResponse updateUser(@RequestBody SysUser user) {
        Boolean update = sysUserService.updateUser(user);
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", update, null);
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse deleteUser(@PathVariable Long id) {
        Boolean delete = sysUserService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", delete, null);
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
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        userTokenDTO.setUserId(userToken.getUserId());
        userTokenDTO.setToken(userToken.getToken());
        userTokenDTO.setExpiresAt(userToken.getExpiresAt());
        userTokenDTO.setStatus(userToken.getStatus());
        userTokenDTO.setRoles(auth.getRoles());
        userTokenDTO.setEmployeeId(auth.getEmployeeId());
        userTokenDTO.setUsername(auth.getUsername());
        userTokenDTO.setMaintenanceUnitId(auth.getMaintenanceUnitId());

        return new BaseResponse(HttpStatus.OK.value(), "登录成功", userTokenDTO, null);
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
