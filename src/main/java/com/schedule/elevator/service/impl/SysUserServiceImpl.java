package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.SysUserMapper;
import com.schedule.elevator.entity.SysUser;
import com.schedule.elevator.service.ISysUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements ISysUserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser register(SysUser user) {
        // 检查用户名是否已存在
        if (this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, user.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 保存
        this.save(user);
        return user; // 返回已保存的用户（含ID）
    }

    @Override
    public Boolean updateUser(SysUser user) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId, user.getId()); // 工单ID
        if (user.getUsername() != null) {
            updateWrapper.set(SysUser::getUsername, user.getUsername());
        }
        if (user.getPassword() != null) {
            updateWrapper.set(SysUser::getPassword, passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRole() != null) {
            updateWrapper.set(SysUser::getRole, user.getRole());
        }
        if (user.getDescription() != null) {
            updateWrapper.set(SysUser::getDescription, user.getDescription());
        }
        return this.update(user, updateWrapper);
    }

    @Override
    public SysUser findByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public SysUser auth(SysUser user) {
        if (user == null) {
            return null;
        }

        SysUser one = this.getOne(new LambdaQueryWrapper<SysUser>()
//                .select(SysUser::getUsername, SysUser::getPassword) // 只查必要字段
                .eq(SysUser::getUsername, user.getUsername()));
        if (one == null) {
            return null;
        }

        if (passwordEncoder.matches(user.getPassword(), one.getPassword())) {
            return one;
        } else {
            return null;
        }
    }
}