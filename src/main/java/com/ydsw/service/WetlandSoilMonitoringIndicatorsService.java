package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.WetlandSoilMonitoringIndicators;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【wetland_soil_monitoring_indicators(湿地土壤监测指标表)】的数据库操作Service
* @createDate 2024-09-27 21:54:08
*/
public interface WetlandSoilMonitoringIndicatorsService extends IService<WetlandSoilMonitoringIndicators> {

    List<WetlandSoilMonitoringIndicators> fetchDataByTimeAndPlant(WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicatorsClass);

    IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicatorsClass);

    void delByIdList(List<Integer> objects, String dateSelected, String filepath);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

    List<String> fetchObservationTimeByDay();
}
