package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.WetlandSoilMonitoringIndicatorsMapper;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.WetlandSoilMonitoringIndicators;
import com.ydsw.service.WetlandSoilMonitoringIndicatorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【wetland_soil_monitoring_indicators(湿地土壤监测指标表)】的数据库操作Service实现
* @createDate 2024-09-27 21:54:08
*/
@Service
public class WetlandSoilMonitoringIndicatorsServiceImpl extends ServiceImpl<WetlandSoilMonitoringIndicatorsMapper, WetlandSoilMonitoringIndicators>
    implements WetlandSoilMonitoringIndicatorsService{

    @Autowired
    private WetlandSoilMonitoringIndicatorsMapper wetlandSoilMonitoringIndicatorsMapper;

    @Override
    public List<WetlandSoilMonitoringIndicators> fetchDataByTimeAndPlant(WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicatorsClass) {
        return wetlandSoilMonitoringIndicatorsMapper.selectByTimeAndPlant(wetlandSoilMonitoringIndicatorsClass);
    }

    @Override
    public IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicatorsClass) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return wetlandSoilMonitoringIndicatorsMapper.getAtmosphereDatasPage(page,wetlandSoilMonitoringIndicatorsClass);
    }

    @Override
    public void delByIdList(List<Integer> idList, String dateSelected, String filepath) {
        wetlandSoilMonitoringIndicatorsMapper.deleteById(idList,dateSelected, filepath);
    }

    @Override
    public List<String> fetchObservationTimeByYear() {
        return wetlandSoilMonitoringIndicatorsMapper.fetchObservationTimeByYear();
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return wetlandSoilMonitoringIndicatorsMapper.fetchObservationTimeByMonth();
    }

    @Override
    public List<String> fetchObservationTimeByDay() {
        return wetlandSoilMonitoringIndicatorsMapper.fetchObservationTimeByDay();
    }
}




