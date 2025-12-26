package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.SysUserDTO;
import com.schedule.elevator.entity.SysUser;

public interface ISysUserService extends IService<SysUser> {

    /**
     * 注册新用户（自动加密密码）
     */
    SysUser register(SysUser user);

    Page<SysUser> querySysUserPage(SysUserDTO query);

    Boolean updateUser(SysUser user);

    /**
     * 根据用户名查询（用于登录）
     */
    SysUser findByUsername(String username);

    SysUser auth(SysUser user);
}