package com.schedule.elevator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.elevator.dto.NearbyMaintenanceUnitDTO;
import com.schedule.elevator.entity.MaintenanceUnit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MaintenanceMapper extends BaseMapper<MaintenanceUnit> {

    /**
     * 查询指定坐标 N 公里范围内的维保单位（使用 Haversine 公式）
     *
     * @param centerLat 中心点纬度
     * @param centerLng 中心点经度
     * @param radiusKm  半径（公里）
     * @return 维保单位列表（按距离升序）
     */
    @Select({
            "<script>",
            "SELECT",
            "  id, site_name, maintenance_company, maintainer_name,",
            "  company_phone, maintainer_phone, company_address,",
            "  latitude, longitude, company_code, company_manager, company_level,",
            "  create_time, update_time,",
            "  (6371 * ACOS(",
            "    LEAST(1.0, GREATEST(-1.0,",
            "      COS(RADIANS(#{centerLat})) *",
            "      COS(RADIANS(latitude)) *",
            "      COS(RADIANS(longitude) - RADIANS(#{centerLng})) +",
            "      SIN(RADIANS(#{centerLat})) *",
            "      SIN(RADIANS(latitude))",
            "    ))",
            "  )) AS distance_km",
            "FROM maintenance",
            "WHERE latitude IS NOT NULL",
            "  AND longitude IS NOT NULL",
            "  AND latitude BETWEEN #{latMin} AND #{latMax}",
            "  AND longitude BETWEEN #{lngMin} AND #{lngMax}",
            "HAVING distance_km &lt;= #{radiusKm}",
            "ORDER BY distance_km",
            "</script>"
    })
    List<NearbyMaintenanceUnitDTO> selectNearby(
            @Param("centerLat") BigDecimal centerLat,
            @Param("centerLng") BigDecimal centerLng,
            @Param("radiusKm") BigDecimal radiusKm,
            @Param("latMin") BigDecimal latMin,
            @Param("latMax") BigDecimal latMax,
            @Param("lngMin") BigDecimal lngMin,
            @Param("lngMax") BigDecimal lngMax
    );
}