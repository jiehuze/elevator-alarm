package com.schedule.elevator.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.ApplyApproveMapper;
import com.schedule.elevator.dto.ApplyApproveQueryDTO;
import com.schedule.elevator.dto.MaintenanceChangeApplyDTO;
import com.schedule.elevator.entity.ApplyApprove;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.entity.MaintenancePersonnel;
import com.schedule.elevator.entity.SysUser;
import com.schedule.elevator.service.IApplyApproveService;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IMaintenancePersonnelService;
import com.schedule.elevator.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 统一审批服务实现
 */
@Service
public class ApplyApproveServiceImpl extends ServiceImpl<ApplyApproveMapper, ApplyApprove>
        implements IApplyApproveService {

    @Autowired
    private ApplyApproveMapper applyApproveMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IMaintenancePersonnelService maintenancePersonnelService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Page<ApplyApprove> queryPage(ApplyApproveQueryDTO queryDTO) {
        // 校验分页参数
        int current = (queryDTO.getCurrent() == null || queryDTO.getCurrent() < 1) ? 1 : queryDTO.getCurrent();
        int size = (queryDTO.getSize() == null || queryDTO.getSize() < 1 || queryDTO.getSize() > 100) ? 10 : queryDTO.getSize();

        Page<ApplyApprove> page = new Page<>(current, size);
        LambdaQueryWrapper<ApplyApprove> queryWrapper = buildQueryWrapper(queryDTO);

        return applyApproveMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitApply(ApplyApprove applyApprove) {
        // 生成申请单号
        String applyNo = generateApplyNo(applyApprove.getApplyType());

        applyApprove.setApplyNo(applyNo);
        applyApprove.setStatus(0); // 待审批

        applyApproveMapper.insert(applyApprove);

        return applyNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Long id, Integer approveUserId, String approveComment) {
        ApplyApprove applyApprove = applyApproveMapper.selectById(id);
        if (applyApprove == null) {
            throw new IllegalArgumentException("申请单不存在");
        }
        if (applyApprove.getStatus() != 0) {
            throw new IllegalArgumentException("该申请单已审批");
        }

        // 获取审批人信息
        SysUser approveUser = sysUserService.getById(approveUserId);
        String approveUserName = approveUser != null ? approveUser.getUsername() : "";

        // 处理申请内容
        processApplyData(applyApprove);

        applyApprove.setStatus(1); // 通过
        applyApprove.setApproveUserId(approveUserId);
        applyApprove.setApproveUserName(approveUserName);
        applyApprove.setApproveComment(approveComment);
        applyApprove.setApproveTime(LocalDateTime.now());

        return applyApproveMapper.updateById(applyApprove) > 0;
    }

    /**
     * 处理申请内容
     * 根据申请类型执行不同的业务逻辑
     */
    private void processApplyData(ApplyApprove applyApprove) {
        if (applyApprove.getApplyType() == null || StringUtils.isBlank(applyApprove.getApplyData())) {
            return;
        }

        switch (applyApprove.getApplyType()) {
            case 1: // 员工变更
                processEmployeeApply(applyApprove.getApplyData());
                break;
            case 2: // 维保公司变更
            case 3: // 注销脱保
                processMaintenanceChangeApply(applyApprove);
                break;
            default:
                throw new IllegalArgumentException("未知的申请类型：" + applyApprove.getApplyType());
        }
    }

    /**
     * 处理员工变更申请
     */
    private void processEmployeeApply(String applyData) {
        MaintenancePersonnel personnel = JSON.parseObject(applyData, MaintenancePersonnel.class);
        if (personnel == null) {
            throw new IllegalArgumentException("员工申请数据格式错误");
        }

        // 校验必填字段
        if (StringUtils.isBlank(personnel.getName())) {
            throw new IllegalArgumentException("员工姓名不能为空");
        }
        if (StringUtils.isBlank(personnel.getPhone())) {
            throw new IllegalArgumentException("员工手机号不能为空");
        }

        // 设置默认值
        if (personnel.getStatus() == null) {
            personnel.setStatus(1);
        }

        // 保存到数据库
        maintenancePersonnelService.save(personnel);
    }

    /**
     * 处理维保单位变更申请
     */
    private void processMaintenanceChangeApply(ApplyApprove applyApprove) {
        MaintenanceChangeApplyDTO changeDTO = JSON.parseObject(applyApprove.getApplyData(), MaintenanceChangeApplyDTO.class);
        if (changeDTO == null) {
            System.out.println("维保单位变更申请数据格式错误");
        }

        // 校验必填字段
        if (changeDTO.getMaintenanceUnitId() == null) {
            System.out.println("维保单位ID不能为空");
        }
        if (StringUtils.isBlank(changeDTO.getMaintenanceUnit())) {
            System.out.println("维保单位名称不能为空");
        }
        if (changeDTO.getElevatorList() == null || changeDTO.getElevatorList().isEmpty()) {
            System.out.println("电梯列表不能为空");
        }

        // 遍历电梯列表，更新每台电梯的维保信息
        for (ElevatorInfo elevatorInfo : changeDTO.getElevatorList()) {
            if (elevatorInfo.getId() == null) {
                System.out.println("电梯ID不能为空");
                continue;
            }
//            if (changeDTO.getMaintenanceUnitChanged() == null) {
//                System.out.println("维保到期状态不能为空");
//                continue;
//            }

            // 根据救援码查询电梯
            ElevatorInfo elevator = new ElevatorInfo();

            if (applyApprove.getApplyType() == 3) { // 注销
                elevator.setMaintenanceUnit("");
                elevator.setMaintenanceUnitId(0l);
                elevator.setMaintenanceType("无");
                elevator.setMaintenanceTeamId(0l);
                elevator.setMaintenancePersonnelName("无");
                elevator.setMaintenancePersonnelId(0l);
                elevator.setUsageStatus(4);
                elevator.setMaintenanceUnitExpired(true);
            } else {
                // 更新电梯维保信息
//                elevator.setMaintenanceType(changeDTO.getMaintenanceType());
                elevator.setMaintenanceUnitId(changeDTO.getMaintenanceUnitId());
                elevator.setMaintenanceUnit(changeDTO.getMaintenanceUnit());
                elevator.setMaintenanceTeamId(changeDTO.getMaintenanceTeamId());
                elevator.setMaintenancePersonnelId(changeDTO.getMaintenancePersonnelId());
                elevator.setMaintenancePersonnelName(changeDTO.getMaintenancePersonnelName());
                elevator.setUsageStatus(1);
                elevator.setMaintenanceUnitExpired(false);
            }

            elevatorInfoService.update(elevator, new LambdaQueryWrapper<ElevatorInfo>().eq(ElevatorInfo::getId, elevatorInfo.getId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reject(Long id, Integer approveUserId, String approveComment) {
        ApplyApprove applyApprove = applyApproveMapper.selectById(id);
        if (applyApprove == null) {
            throw new IllegalArgumentException("申请单不存在");
        }
        if (applyApprove.getStatus() != 0) {
            throw new IllegalArgumentException("该申请单已审批");
        }

        // 获取审批人信息
        SysUser approveUser = sysUserService.getById(approveUserId);
        String approveUserName = approveUser != null ? approveUser.getUsername() : "";

        applyApprove.setStatus(2); // 拒绝
        applyApprove.setApproveUserId(approveUserId);
        applyApprove.setApproveUserName(approveUserName);
        applyApprove.setApproveComment(approveComment);
        applyApprove.setApproveTime(LocalDateTime.now());
        applyApprove.setUpdatedAt(LocalDateTime.now());

        return applyApproveMapper.updateById(applyApprove) > 0;
    }

    @Override
    public ApplyApprove getByApplyNo(String applyNo) {
        LambdaQueryWrapper<ApplyApprove> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplyApprove::getApplyNo, applyNo);
        return applyApproveMapper.selectOne(queryWrapper);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<ApplyApprove> buildQueryWrapper(ApplyApproveQueryDTO queryDTO) {
        LambdaQueryWrapper<ApplyApprove> query = new LambdaQueryWrapper<>();

        // 申请单号模糊查询
        query.like(StringUtils.isNotBlank(queryDTO.getApplyNo()), ApplyApprove::getApplyNo, queryDTO.getApplyNo());

        // 申请类型
        query.eq(queryDTO.getApplyType() != null, ApplyApprove::getApplyType, queryDTO.getApplyType());

        // 申请人ID
        query.eq(queryDTO.getApplyUserId() != null, ApplyApprove::getApplyUserId, queryDTO.getApplyUserId());

        // 审批状态
        query.eq(queryDTO.getStatus() != null, ApplyApprove::getStatus, queryDTO.getStatus());

        // 时间范围
        query.ge(queryDTO.getStartTime() != null, ApplyApprove::getCreatedAt, queryDTO.getStartTime());
        query.le(queryDTO.getEndTime() != null, ApplyApprove::getCreatedAt, queryDTO.getEndTime());

        // 按创建时间倒序
        query.orderByDesc(ApplyApprove::getCreatedAt);

        return query;
    }

    /**
     * 生成申请单号
     * 格式：类型(1位) + 日期(8位) + 随机数(4位)
     * 例如：1202503260001
     */
    private String generateApplyNo(Integer applyType) {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return applyType + dateStr + randomStr;
    }
}
