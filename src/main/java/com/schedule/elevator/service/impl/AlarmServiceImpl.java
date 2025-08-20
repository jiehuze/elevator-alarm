package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.entity.Alarm;
import com.schedule.elevator.service.IAlarmService;
import org.springframework.stereotype.Service;

@Service
public class AlarmServiceImpl extends ServiceImpl<com.schedule.elevator.dao.mapper.AlarmMapper, Alarm> implements IAlarmService {

}