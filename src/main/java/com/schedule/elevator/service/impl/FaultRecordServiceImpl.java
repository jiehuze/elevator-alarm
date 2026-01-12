package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.IFaultRecordMapper;
import com.schedule.elevator.dto.FaultResultDTO;
import com.schedule.elevator.entity.FaultCategory;
import com.schedule.elevator.entity.FaultRecord;
import com.schedule.elevator.service.IFaultCategoryService;
import com.schedule.elevator.service.IFaultRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FaultRecordServiceImpl extends ServiceImpl<IFaultRecordMapper, FaultRecord>
        implements IFaultRecordService {

    @Autowired
    private IFaultCategoryService faultCategoryService;

    @Override
    public List<FaultRecord> getByOrderNo(String orderNo) {
        LambdaQueryWrapper<FaultRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRecord::getOrderNo, orderNo);
        return this.list(wrapper);
    }

    @Override
    public List<FaultRecord> getByRootCode(String rootCode) {
        LambdaQueryWrapper<FaultRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRecord::getRootCode, rootCode);
        return this.list(wrapper);
    }

    @Override
    public boolean removeByOrderNo(String orderNo) {
        LambdaQueryWrapper<FaultRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRecord::getOrderNo, orderNo);
        return this.remove(wrapper);
    }

    @Override
    public List<Map<String, Object>> countByRootCodeInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.countByRootCodeInTimeRange(startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> countBySubCodeInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.countBySubCodeInTimeRange(startTime, endTime);
    }

    @Override
    public Long countAllTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<FaultRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(FaultRecord::getCreatedAt, startTime, endTime);
        return this.count(wrapper);
    }

    @Override
    public List<FaultResultDTO> statisticalFault(LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal allTotals = new BigDecimal(this.countAllTimeRange(startTime, endTime));

        if (allTotals.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        HashMap<String, FaultResultDTO> faultResultDTOHashMap = new HashMap<>();
        List<Map<String, Object>> rootMaps = baseMapper.countByRootCodeInTimeRange(startTime, endTime);
        List<Map<String, Object>> subMaps = baseMapper.countBySubCodeInTimeRange(startTime, endTime);
        // 创建新的列表存储合并结果
        List<Map<String, Object>> combinedList = new ArrayList<>();
        combinedList.addAll(rootMaps);
        combinedList.addAll(subMaps);

        for (Map<String, Object> rootMap : combinedList) {
            String faultCode = (String) rootMap.get("faultCode");
            Long faultCount = (Long) rootMap.get("faultCount");
            FaultResultDTO faultResultDTO = new FaultResultDTO().setFaultCode(faultCode).setTotals(faultCount);
            faultResultDTOHashMap.put(faultCode, faultResultDTO);
        }

        ArrayList<FaultResultDTO> list = new ArrayList<>();
        List<FaultCategory> faultCategoryTree = faultCategoryService.getFaultCategoryTree();
        for (FaultCategory faultCategory : faultCategoryTree) {
            FaultResultDTO rootFaultDTO = new FaultResultDTO();
            rootFaultDTO.setFaultCode(faultCategory.getFaultCode());
            rootFaultDTO.setFaultName(faultCategory.getFaultAnalysis());
            if (faultResultDTOHashMap.get(faultCategory.getFaultCode()) != null) {
//                System.out.println("1 ============= faultResultDTOHashMap.get(faultCategory.getFaultCode()): " + faultResultDTOHashMap.get(faultCategory.getFaultCode()));
                rootFaultDTO.setTotals(faultResultDTOHashMap.get(faultCategory.getFaultCode()).getTotals());
                BigDecimal totals = new BigDecimal(rootFaultDTO.getTotals());

                rootFaultDTO.setPercent(totals.multiply(new BigDecimal("100")).divide(allTotals, 2, BigDecimal.ROUND_HALF_UP));
            } else {
                rootFaultDTO.setTotals(0L);
                rootFaultDTO.setPercent(new BigDecimal("0"));
            }

            List<FaultCategory> children = faultCategory.getChildren();
            ArrayList<FaultResultDTO> childFaultResult = new ArrayList<>();
            for (FaultCategory child : children) {
                FaultResultDTO faultResultDTO = faultResultDTOHashMap.get(child.getFaultCode());
                if (faultResultDTO == null) {
                    faultResultDTO = new FaultResultDTO();
                }

                faultResultDTO.setFaultName(child.getFaultAnalysis());
                faultResultDTO.setFaultCode(child.getFaultCode());
                if (faultResultDTOHashMap.get(child.getFaultCode()) != null) {
//                    System.out.println("2 ============= faultResultDTOHashMap.get(child.getFaultCode()): " + faultResultDTOHashMap.get(child.getFaultCode()));
                    faultResultDTO.setTotals(faultResultDTOHashMap.get(child.getFaultCode()).getTotals());
                    BigDecimal totals = new BigDecimal(faultResultDTO.getTotals());
                    faultResultDTO.setPercent(totals.multiply(new BigDecimal("100")).divide(allTotals, 2, BigDecimal.ROUND_HALF_UP));
                } else {
                    faultResultDTO.setTotals(0L);
                    faultResultDTO.setPercent(new BigDecimal("0"));
                }

                childFaultResult.add(faultResultDTO);
            }
            rootFaultDTO.setChild(childFaultResult);
            list.add(rootFaultDTO);
        }


        return list;
    }
}
