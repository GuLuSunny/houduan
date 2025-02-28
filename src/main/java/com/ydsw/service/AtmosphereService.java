package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.Atmosphere;

import java.util.List;
import java.util.Map;

/**
* @author lenovo
* @description 针对表【atmosphere(气象数据表)】的数据库操作Service
* @createDate 2024-08-30 19:35:58
*/
public interface AtmosphereService extends IService<Atmosphere> {

    List<Atmosphere> fetchDataByObservationTimeAndDevice(String time, String device);

    List<String> fetchObservationTime();

    Atmosphere findCurrentAtmosphereByDevice(String device,String time);

    IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, Atmosphere atmosphereClass);

    void delByIdList(List<Integer> idList, String dateSelected, String filepath, String deviceId);

    IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens);

    void updateOpenStatusByFilepathsAndDate(List<Integer> filepaths, String dateSelected, int openValue, String filepath);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

    IPage<Map<String,Object>> fetchFilepathByObservationTimeAndClassName(Integer currentPage,Integer pageSize, List<Integer> idList,String observationTimeBegin,String observationTimeEnd, String className, String filepath,String type, String typeDetail,String deviceId,String deviceName);
}
