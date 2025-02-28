package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.WaterPhysicochemistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author yanzhimeng
* @description 针对表【water_physicochemistry】的数据库操作Mapper
* @createDate 2024-08-23 09:59:45
* @Entity com.ydsw.domain.WaterPhysicochemistry
*/


@Mapper
public interface WaterPhysicochemistryMapper extends BaseMapper<WaterPhysicochemistry> {

    List<WaterPhysicochemistry> getDatas(@Param("time") String time,@Param("device") String device);

    IPage<Map<String,Object>> getWaterPhysicochemistryDatasPage(IPage<?> page,@Param("waterPhysicochemistry") WaterPhysicochemistry waterPhysicochemistry);

    List<String> selectObservationTime();

    void deleteById(@Param("idList")List<Integer> idList,@Param("dateSelected")String dateSelected, @Param("filepath")String filepath,@Param("deviceId") String deviceId);

    IPage<Map<String, Object>> findDataPageByCondition(IPage<SpectralReflectance> page, @Param("time")String time,@Param("filepath") String filepath, @Param("opens")Integer opens);

    int updateOpenStatusByFilepathsAndDate(@Param("idList") List<Integer> idList, @Param("dateSelected") String dateSelected, @Param("openValue") int openValue,@Param("filepath") String filepath);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

    List<String> fetchObservationTimeByDay();
}




