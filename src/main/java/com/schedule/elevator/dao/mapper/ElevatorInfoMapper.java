package com.schedule.elevator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.elevator.dto.BrandElevatorCountDTO;
import com.schedule.elevator.dto.BrandMarketAnalysisDTO;
import com.schedule.elevator.dto.ProjectTypeCountDTO;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ElevatorInfoMapper extends BaseMapper<ElevatorInfo> {
    @Select("<script>" +
            "SELECT " +
            "    elevator_type AS elevatorType, " +
            "    COUNT(*) AS elevatorCount " +
            "FROM elevator " +
            "WHERE elevator_type IS NOT NULL " +
            "  AND elevator_type != '' " +
//            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'> " +
//            "  AND created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
//            "</if> " +
            "<if test='searchDTO.district != null and searchDTO.district != \"\"'> " +
            "  AND district = #{searchDTO.district} " +
            "</if> " +
            "GROUP BY elevator_type" +
            "</script>")
    List<Map<String, Object>> countByElevatorType(@Param("searchDTO") SearchDTO searchDTO);

    @Select("<script>" +
            "SELECT " +
            "    district AS district, " +
            "    COUNT(*) AS elevatorCount, " +
            "    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM elevator " +
            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'> " +
            "  WHERE created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if>), 2) AS percentage " +
            "FROM elevator " +
            "WHERE district IS NOT NULL " +
            "  AND district != '' " +
            "<if test='searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'> " +
            "  AND created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} " +
            "</if> " +
            "GROUP BY district" +
            "</script>")
    List<Map<String, Object>> countByDistrict(@Param("searchDTO") SearchDTO searchDTO);

    @Select("<script>" +
            "SELECT " +
            "    COUNT(*) AS totalElevators, " +
            "    SUM(CASE WHEN created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd} THEN 1 ELSE 0 END) AS newElevators " +
            "FROM elevator " +
            "WHERE created_at IS NOT NULL " +
            "  AND #{searchDTO.createTimeStart} IS NOT NULL " +
            "  AND #{searchDTO.createTimeEnd} IS NOT NULL" +
            "</script>")
    Map<String, Object> countNewElevators(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "  project_type AS projectCode, ",
            "  COUNT(*) AS count ",
            "FROM elevator ",
            "WHERE project_type IS NOT NULL ",
            "  AND project_type != '' ",
            "<if test='searchDTO != null and searchDTO.createTimeStart != null and searchDTO.createTimeEnd != null'>",
            "  AND created_at BETWEEN #{searchDTO.createTimeStart} AND #{searchDTO.createTimeEnd}",
            "</if>",
            "<if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "  AND district = #{searchDTO.district}",
            "</if>",
            "GROUP BY project_type ",
            "</script>"
    })
    List<ProjectTypeCountDTO> countElevatorsByProjectType(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "<![CDATA[",
            "  COUNT(DISTINCT brand) AS totalBrands, ",
            "  COUNT(CASE WHEN brand_counts.brand_count <= 10 THEN 1 END) AS smallBrandsCount, ",
            "  COUNT(*) AS totalBrandsAll, ",
            "  ROUND(COUNT(CASE WHEN brand_counts.brand_count <= 10 THEN 1 END) * 100.0 / COUNT(*), 2) AS smallBrandPercentage ",
            "]]>",
            "FROM (",
            "  SELECT brand, COUNT(*) AS brand_count ",
            "  FROM elevator ",
            "  WHERE brand IS NOT NULL AND brand != '' ",
            "    <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "      AND district = #{searchDTO.district}",
            "    </if>",
            "  GROUP BY brand",
            ") brand_counts",
            "</script>"
    })
    BrandMarketAnalysisDTO getBrandMarketAnalysis(@Param("searchDTO") SearchDTO searchDTO);

    @Select({
            "<script>",
            "SELECT ",
            "  brand AS brandName, ",
            "  COUNT(*) AS elevatorCount ",
            "FROM elevator ",
            "WHERE brand IS NOT NULL AND brand != '' ",
            "  <if test='searchDTO != null and searchDTO.district != null and searchDTO.district != \"\"'>",
            "    AND district = #{searchDTO.district}",
            "  </if>",
            "GROUP BY brand ",
            "ORDER BY COUNT(*) DESC ",
            "LIMIT 5",
            "</script>"
    })
    List<BrandElevatorCountDTO> getTop5BrandElevatorCounts(@Param("searchDTO") SearchDTO searchDTO);
}
