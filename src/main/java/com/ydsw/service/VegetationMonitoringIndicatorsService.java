package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.VegetationMonitoringIndicators;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【vegetation_monitoring_indicators(植被监测指标表)】的数据库操作Service
* @createDate 2024-09-27 22:01:51
*/
public interface VegetationMonitoringIndicatorsService extends IService<VegetationMonitoringIndicators> {
    List<VegetationMonitoringIndicators> fetchDataByObservationTime(String time, String plant,Integer deviceId);

    IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, VegetationMonitoringIndicators vegetationMonitoringIndicatorsClass);

    void delByIdList(List<Integer> objects, String dateSelected, String filepath);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

    List<String> fetchObservationTimeByDay();

    List<String> getDistinctVegetationSpecies();

}
