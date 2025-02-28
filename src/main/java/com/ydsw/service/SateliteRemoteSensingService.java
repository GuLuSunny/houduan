package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.SateliteRemoteSensing;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【satelite_remote_sensing(卫星遥感)】的数据库操作Service
* @createDate 2024-11-06 16:50:32
*/
public interface SateliteRemoteSensingService extends IService<SateliteRemoteSensing> {

    IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens);

    void updateOpenStatusByFilepathsAndDate(List<Integer> filepaths, String dateSelected, int openValue, String filepath);

    IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, SateliteRemoteSensing sateliteRemoteSensingClass);

    void delByIdList(List<Integer> idList, String dateSelected, String filepath, String deviceId,String type);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

}
