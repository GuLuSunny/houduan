package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.WetlandSoilMonitoringIndicators;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【wetland_soil_monitoring_indicators(湿地土壤监测指标表)】的数据库操作Mapper
* @createDate 2024-09-27 21:54:08
* @Entity com.ydsw.domain.WetlandSoilMonitoringIndicators
*/
@Mapper
public interface WetlandSoilMonitoringIndicatorsMapper extends BaseMapper<WetlandSoilMonitoringIndicators> {

    List<WetlandSoilMonitoringIndicators> selectByTimeAndPlant(@Param("wetlandSoilMonitoringIndicatorsClass") WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicatorsClass);

    IPage<Map<String, Object>> getAtmosphereDatasPage(IPage<?> page,@Param("wetlandSoilMonitoringIndicatorsClass") WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicatorsClass);

    void deleteById(@Param("idList")List<Integer> idList,@Param("dateSelected")String dateSelected, @Param("filepath")String filepath);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

    List<String> fetchObservationTimeByDay();
}




