package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.WaterPhysicochemistryMapper;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.WaterPhysicochemistry;
import com.ydsw.service.WaterPhysicochemistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author yanzhimeng
* @description 针对表【water_physicochemistry】的数据库操作Service实现
* @createDate 2024-08-23 09:59:45
*/
@Service
public class WaterPhysicochemistryServiceImpl extends ServiceImpl<WaterPhysicochemistryMapper, WaterPhysicochemistry>
    implements WaterPhysicochemistryService{

        @Autowired
        private WaterPhysicochemistryMapper waterPhysicochemistryMapper;

        public List<WaterPhysicochemistry> fetchDataByObservationTimeAndDevice(String time,String device) {
            return waterPhysicochemistryMapper.getDatas(time,device); // 直接调用 Mapper 方法获取数据
        }

    @Override
    public List<String> fetchObservationTime() {
        return waterPhysicochemistryMapper.selectObservationTime();
    }

    @Override
    public IPage<Map<String,Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, WaterPhysicochemistry wp)
    {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return waterPhysicochemistryMapper.getWaterPhysicochemistryDatasPage(page,wp);
    }

    @Override
    public void delByIdList(List<Integer> idList, String dateSelected, String filepath,String deviceId)
    {
        waterPhysicochemistryMapper.deleteById(idList,dateSelected,filepath,deviceId);
    }

    @Override
    public IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return waterPhysicochemistryMapper.findDataPageByCondition(page,time,filepath,  opens);
    }

    @Override
    public void updateOpenStatusByFilepathsAndDate(List<Integer> idList, String dateSelected, int openValue, String filepath) {
        waterPhysicochemistryMapper.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue,filepath);

    }

    @Override
    public List<String> fetchObservationTimeByYear() {
        return waterPhysicochemistryMapper.fetchObservationTimeByYear();
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return waterPhysicochemistryMapper.fetchObservationTimeByMonth();
    }

    @Override
    public List<String> fetchObservationTimeByDay() {
        return waterPhysicochemistryMapper.fetchObservationTimeByDay();
    }
}




