package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.TbFlow;

import java.util.List;
import java.util.Map;

/**
 * @author lenovo
 * @description 针对表【tb_flow(伊河流量登记表)】的数据库操作Service
 * @createDate 2024-08-24 17:04:03
 */
public interface TbFlowService extends IService<TbFlow> {
    List<Map<String,Object>> selectFlowByObservationTimeAndDeviceId( String observationTime,Integer deviceId);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

    List<Map<String, String>> fetchFlowByTimePeriodAndDevice(String timeEarliest, String timeLatest, String device);

    Integer getDeviceIdByDeviceName(String deviceName);

    List<Map<String, Object>> findFlowByYearAndDevice(String year, String deviceId);

    List<Map<String, Object>> findFlowByYearMonthAndDevice(String yearMonth, String deviceId);

    IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, TbFlow tbFlowClass);

    void delByIdList(List<Integer> idList, String dateSelected, String filepath, String deviceId);

    IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens);

    void updateOpenStatusByFilepathsAndDate(List<Integer> filepaths, String dateSelected, int openValue, String filepath);

    List<String> fetchObservationTimeByYear1(String filepath);

    List<String> fetchObservationTimeByMonth1(String filepath);
}
