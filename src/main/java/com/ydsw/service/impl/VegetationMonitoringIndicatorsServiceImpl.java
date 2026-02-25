package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.VegetationMonitoringIndicatorsMapper;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.VegetationMonitoringIndicators;
import com.ydsw.service.VegetationMonitoringIndicatorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【vegetation_monitoring_indicators(植被监测指标表)】的数据库操作Service实现
* @createDate 2024-09-27 22:01:51
*/
@Service
public class VegetationMonitoringIndicatorsServiceImpl extends ServiceImpl<VegetationMonitoringIndicatorsMapper, VegetationMonitoringIndicators>
    implements VegetationMonitoringIndicatorsService{
    @Autowired
    private VegetationMonitoringIndicatorsMapper vegetationMonitoringIndicatorsMapper;

    @Override
    public List<VegetationMonitoringIndicators> fetchDataByObservationTime(String time, String plant,Integer deviceId) {
        return vegetationMonitoringIndicatorsMapper.selectByTime(time,plant,deviceId);
    }

    @Override
    public IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, VegetationMonitoringIndicators vegetationMonitoringIndicatorsClass) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return vegetationMonitoringIndicatorsMapper.getAtmosphereDatasPage(page,vegetationMonitoringIndicatorsClass);
    }

    @Override
    public void delByIdList(List<Integer> idList, String dateSelected, String filepath) {
        vegetationMonitoringIndicatorsMapper.deleteById(idList,dateSelected, filepath);
    }

    @Override
    public List<String> fetchObservationTimeByYear() {
        return vegetationMonitoringIndicatorsMapper.fetchObservationTimeByYear();
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return vegetationMonitoringIndicatorsMapper.fetchObservationTimeByMonth();
    }

    @Override
    public List<String> fetchObservationTimeByDay() {
        return vegetationMonitoringIndicatorsMapper.fetchObservationTimeByDay();
    }

    // 新增：实现接口中定义的 getDistinctVegetationSpecies() 方法
    @Override
    public List<String> getDistinctVegetationSpecies() {
        // 直接调用 Mapper 方法（该方法已绑定 XML 中的 SQL）
        return vegetationMonitoringIndicatorsMapper.selectDistinctVegetationSpecies();
    }


}




