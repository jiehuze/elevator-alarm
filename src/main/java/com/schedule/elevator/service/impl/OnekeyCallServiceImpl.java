package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.OnekeyCallMapper;
import com.schedule.elevator.entity.OnekeyCall;
import com.schedule.elevator.service.IOnekeyCallService;
import org.springframework.stereotype.Service;

@Service
public class OnekeyCallServiceImpl extends ServiceImpl<OnekeyCallMapper, OnekeyCall>
        implements IOnekeyCallService {
}
