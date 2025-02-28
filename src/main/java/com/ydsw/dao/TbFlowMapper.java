package com.ydsw.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.TbFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yinshuoran* @description 针对表【tb_flow(伊河流量登记表)】的数据库操作Mapper
 * @createDate 2024-08-25 15:10:36
 * @Entity generator.domain.TbFlow
 */




@Mapper
public interface TbFlowMapper extends BaseMapper<TbFlow> {
    //根据具体时间查询
    //@Select("SELECT * FROM tb_flow WHERE observation_time = #{time}")
    //List<TbFlow> selectFlowByObservationTime(@Param("time") String observationTime);
    List<Map<String,Object>> selectFlowByObservationTimeAndDeviceId(@Param("observationTime") String observationTime,@Param("deviceId") Integer deviceId);
    List<Map<String,String>> selectFlowByObservationTime2(@Param("timeEarliest") String timeEarliest, @Param("timeLatest") String timeLatest, @Param("device") String device);

    List<String> fetchObservationTimeByYear(@Param("filepath") String filepath);

    List<String> fetchObservationTimeByMonth(@Param("filepath") String filepath);

    List<Map<String, Object>> findFlowByYearAndDevice(@Param("year")String year,@Param("deviceId") String deviceId);

    List<Map<String, Object>> findFlowByYearMonthAndDevice(@Param("yearMonth")String yearMonth, @Param("deviceId")String deviceId);

    Integer getDeviceIdByDeviceName(@Param("deviceName") String deviceName);

    void deleteById(@Param("idList")List<Integer> idList,@Param("dateSelected")String dateSelected, @Param("filepath")String filepath,@Param("deviceId")String deviceId);

    IPage<Map<String,Object>> getTbFlowDatasPage(IPage<?> page, @Param("tbFlowClass") TbFlow tbFlowClass);

    IPage<Map<String, Object>> findDataPageByCondition(IPage<SpectralReflectance> page, @Param("time")String time,@Param("filepath") String filepath, @Param("opens")Integer opens);


    int updateOpenStatusByFilepathsAndDate(@Param("idList") List<Integer> idList, @Param("dateSelected") String dateSelected, @Param("openValue") int openValue,@Param("filepath") String filepath);
}

