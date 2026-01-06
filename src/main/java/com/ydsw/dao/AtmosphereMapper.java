package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.Atmosphere;
import com.ydsw.domain.SpectralReflectance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author lenovo
* @description 针对表【atmosphere(气象数据表)】的数据库操作Mapper
* @createDate 2024-08-30 19:35:58
* @Entity com.ydsw.domain.Atmosphere
*/
@Mapper
public interface AtmosphereMapper extends BaseMapper<Atmosphere> {

    List<Atmosphere> selectByTimeAndDevice(@Param("time") String time, @Param("device") String device);

    List<String> selectObservationTime();

    Atmosphere findCurrentAtmosphereByDevice( @Param("device") String device,@Param("time") String time);

    IPage<Map<String,Object>> getAtmosphereDatasPage(IPage<?> page,@Param("atmosphereClass") Atmosphere atmosphereClass);

    void deleteById(@Param("idList")List<Integer> idList,@Param("dateSelected")String dateSelected, @Param("filepath")String filepath,@Param("deviceId") String deviceId);

    IPage<Map<String, Object>> findDataPageByCondition(IPage<SpectralReflectance> page, @Param("time")String time,@Param("filepath") String filepath, @Param("opens")Integer opens);

    int updateOpenStatusByFilepathsAndDate(@Param("idList") List<Integer> idList, @Param("dateSelected") String dateSelected, @Param("openValue") int openValue,@Param("filepath") String filepath);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

    IPage<Map<String,Object>> fetchFilepathByObservationTimeAndClassName(IPage<?> page,@Param("idList") List<Integer> idList,@Param("observationTimeBegin")String observationTimeBegin,@Param("observationTimeEnd")String observationTimeEnd,@Param("className")String className,@Param("filepath") String filepath,@Param("type") String type,@Param("typeDetail") String typeDetail,@Param("deviceId") String deviceId,@Param("deviceName")String deviceName);

    boolean updateByCondition(@Param("atmosphereClass") Atmosphere atmosphere);
}




