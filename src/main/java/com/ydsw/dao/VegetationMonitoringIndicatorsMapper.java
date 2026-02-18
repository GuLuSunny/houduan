package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.VegetationMonitoringIndicators;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【vegetation_monitoring_indicators(植被监测指标表)】的数据库操作Mapper
* @createDate 2024-09-27 22:01:51
* @Entity com.ydsw.domain.VegetationMonitoringIndicators
*/
@Mapper
public interface VegetationMonitoringIndicatorsMapper extends BaseMapper<VegetationMonitoringIndicators> {
    List<VegetationMonitoringIndicators> selectByTime(@Param("time") String time, @Param("plant") String plant,@Param("deviceId")Integer deviceId);

    IPage<Map<String, Object>> getAtmosphereDatasPage(IPage<?> page,@Param("vegetationMonitoringIndicatorsClass") VegetationMonitoringIndicators vegetationMonitoringIndicatorsClass);

    void deleteById(@Param("idList")List<Integer> idList,@Param("dateSelected")String dateSelected, @Param("filepath")String filepath);

    List<String> fetchObservationTimeByDay();

    List<String> fetchObservationTimeByMonth();

    List<String> fetchObservationTimeByYear();

    @Select("SELECT DISTINCT vegetation_species FROM vegetation_monitoring_indicators")
    List<String> selectDistinctVegetationSpecies();
}




