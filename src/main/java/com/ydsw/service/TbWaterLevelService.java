package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.TbWaterLevel;

import java.util.List;
import java.util.Map;


/**
 * @author zhang
 * //@description 针对表【tb_water_level(陆浑水库逐日库水位登记表)】的数据库操作Service
 * //@createDate 2024-07-26 16:09:18
 */
public interface TbWaterLevelService extends IService<TbWaterLevel> {
    List<Map<String, Object>> findWaterLevelByObservationTime(String observationTime,Integer deviceId);

    List<Map<String, Object>> fetchWaterLevelByTimePeriod(String timeEarliest, String timeLatest,Integer deviceId);

    /**
     * 根据id列表和条件删除
     * @param idList id列表
     * @param observationTime 日期
     * @param filepath  文件名
     */
    void delByIdList(List<Integer> idList, String observationTime, String filepath);

    IPage<Map<String, Object>> getWaterLevelByPage(int pageNo, int pageSize, TbWaterLevel tbWaterLevel);

    List<String> fetchObservationTimeByYear();
    List<String> fetchObservationTimeByYear1(String filepath);
    List<String> fetchObservationTimeByMonth();

    IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens);

    void updateOpenStatusByFilepathsAndDate(List<Integer> filepaths, String dateSelected, int openValue, String filepath);

    List<String> fetchObservationTimeByMonth1(String filepath);
}
