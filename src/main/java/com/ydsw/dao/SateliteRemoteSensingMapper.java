package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.SateliteRemoteSensing;
import com.ydsw.domain.SpectralReflectance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【satelite_remote_sensing(卫星遥感)】的数据库操作Mapper
* @createDate 2024-11-06 16:50:32
* @Entity generator.domain.SateliteRemoteSensing
*/
@Mapper
public interface SateliteRemoteSensingMapper extends BaseMapper<SateliteRemoteSensing> {

    IPage<Map<String, Object>> findDataPageByCondition(IPage<SpectralReflectance> page, @Param("time")String time, @Param("filepath") String filepath, @Param("opens")Integer opens);



    int updateOpenStatusByFilepathsAndDate(@Param("idList") List<Integer> idList, @Param("dateSelected") String dateSelected, @Param("openValue") int openValue,@Param("filepath") String filepath);

    IPage<Map<String, Object>> getSateliteRemoteSensingDatasPage(IPage<?> page, @Param("sateliteRemoteSensingClass")SateliteRemoteSensing sateliteRemoteSensingClass);

    void deleteById(@Param("idList")List<Integer> idList,@Param("dateSelected")String dateSelected, @Param("filepath")String filepath,@Param("deviceName") String deviceId,@Param("type") String type);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();
}




