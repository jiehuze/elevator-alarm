package com.schedule.excel;

import com.schedule.elevator.entity.MaintenanceUnit;

public class MaintenanceExcelConverter {

//    public static MaintenanceTemplateExcel toDto(MaintenanceUnit entity) {
//        if (entity == null) return null;
//        MaintenanceTemplateExcel dto = new MaintenanceTemplateExcel();
//        dto.setSiteName(entity.getSiteName());
//        dto.setMaintenanceCompany(entity.getMaintenanceCompany());
//        dto.setMaintainerName(entity.getMaintainerName());
//        dto.setCompanyPhone(entity.getCompanyPhone());
//        dto.setMaintainerPhone(entity.getMaintainerPhone());
//        dto.setProvince(entity.getProvince());
//        dto.setCity(entity.getCity());
//        dto.setDistrict(entity.getDistrict());
//        dto.setCompanyAddress(entity.getCompanyAddress());
//        dto.setLatitude(entity.getLatitude());
//        dto.setLongitude(entity.getLongitude());
//        dto.setCompanyCode(entity.getCompanyCode());
//        dto.setCompanyManager(entity.getCompanyManager());
//
//        // Integer → String 映射
//        if (entity.getCompanyLevel() != null) {
//            switch (entity.getCompanyLevel()) {
//                case 1 -> dto.setCompanyLevel("初级");
//                case 2 -> dto.setCompanyLevel("中级");
//                case 3 -> dto.setCompanyLevel("高级");
//                default -> dto.setCompanyLevel("未知");
//            }
//        }
//
//        return dto;
//    }
//
//    public static MaintenanceUnit toEntity(MaintenanceTemplateExcel dto) {
//        if (dto == null) return null;
//        MaintenanceUnit entity = new MaintenanceUnit();
//        entity.setSiteName(dto.getSiteName());
//        entity.setMaintenanceCompany(dto.getMaintenanceCompany());
//        entity.setMaintainerName(dto.getMaintainerName());
//        entity.setCompanyPhone(dto.getCompanyPhone());
//        entity.setMaintainerPhone(dto.getMaintainerPhone());
//        entity.setProvince(dto.getProvince());
//        entity.setCity(dto.getCity());
//        entity.setDistrict(dto.getDistrict());
//        entity.setCompanyAddress(dto.getCompanyAddress());
//        entity.setLatitude(dto.getLatitude());
//        entity.setLongitude(dto.getLongitude());
//        entity.setCompanyCode(dto.getCompanyCode());
//        entity.setCompanyManager(dto.getCompanyManager());
//
//        // String → Integer 映射（支持数字或文字）
//        String levelStr = dto.getCompanyLevel();
//        if ("初级".equals(levelStr)) {
//            entity.setCompanyLevel(1);
//        } else if ("中级".equals(levelStr)) {
//            entity.setCompanyLevel(2);
//        } else if ("高级".equals(levelStr)) {
//            entity.setCompanyLevel(3);
//        } else if (levelStr != null) {
//            try {
//                // 兼容用户直接填 1/2/3
//                entity.setCompanyLevel(Integer.valueOf(levelStr.trim()));
//            } catch (NumberFormatException ignored) {
//                entity.setCompanyLevel(null); // 或抛异常
//            }
//        }
//
//        return entity;
//    }
}