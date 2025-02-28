package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.WaterPhysicochemistry;

import java.util.List;
import java.util.Map;

/**
* @author yanzhimeng
* @description 针对表【water_physicochemistry】的数据库操作Service
* @createDate 2024-08-23 09:59:45
*/
public interface WaterPhysicochemistryService extends IService<WaterPhysicochemistry> {

    public List<WaterPhysicochemistry> fetchDataByObservationTimeAndDevice(String time,String device);

    IPage<Map<String,Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, WaterPhysicochemistry wp);

    List<String> fetchObservationTime();

    void delByIdList(List<Integer> idList, String dateSelected, String filepath,String deviceId);

    IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens);

    void updateOpenStatusByFilepathsAndDate(List<Integer> filepaths, String dateSelected, int openValue, String filepath);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

    List<String> fetchObservationTimeByDay();
}
