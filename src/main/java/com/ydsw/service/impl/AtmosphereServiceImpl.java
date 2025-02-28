package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.AtmosphereMapper;
import com.ydsw.domain.Atmosphere;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.service.AtmosphereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author lenovo
* @description 针对表【atmosphere(气象数据表)】的数据库操作Service实现
* @createDate 2024-08-30 19:35:58
*/
@Service
public class AtmosphereServiceImpl extends ServiceImpl<AtmosphereMapper, Atmosphere>
    implements AtmosphereService{
    @Autowired
    private AtmosphereMapper atmosphereMapper;


    @Override
    public List<Atmosphere> fetchDataByObservationTimeAndDevice(String time, String device) {
        return atmosphereMapper.selectByTimeAndDevice(time,device);
    }

    @Override
    public List<String> fetchObservationTime() {
        return  atmosphereMapper.selectObservationTime();
    }

    @Override
    public Atmosphere findCurrentAtmosphereByDevice(String device,String time) {
        return atmosphereMapper.findCurrentAtmosphereByDevice(device,time);
    }

    @Override
    public IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, Atmosphere atmosphereClass) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return atmosphereMapper.getAtmosphereDatasPage(page,atmosphereClass);
    }

    @Override
    public void delByIdList(List<Integer> idList, String dateSelected, String filepath, String deviceId) {
        atmosphereMapper.deleteById(idList,dateSelected,filepath,deviceId);
    }

    @Override
    public IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return atmosphereMapper.findDataPageByCondition(page,time,filepath,  opens);
    }

    @Override
    public void updateOpenStatusByFilepathsAndDate(List<Integer> idList, String dateSelected, int openValue, String filepath) {
       atmosphereMapper.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue,filepath);

    }

    @Override
    public List<String> fetchObservationTimeByYear() {
        return atmosphereMapper.fetchObservationTimeByYear();
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return atmosphereMapper.fetchObservationTimeByMonth();
    }

    @Override
    public     IPage<Map<String,Object>> fetchFilepathByObservationTimeAndClassName(Integer currentPage,Integer pageSize, List<Integer> idList,String observationTimeBegin,String observationTimeEnd, String className, String filepath,String type, String typeDetail,String deviceId,String deviceName)
    {
        IPage<Map<String,Object>> page = new Page<>(currentPage, pageSize);
        return atmosphereMapper.fetchFilepathByObservationTimeAndClassName(page,idList,observationTimeBegin,observationTimeEnd,className,filepath,type,typeDetail,deviceId,deviceName);
    }
}






