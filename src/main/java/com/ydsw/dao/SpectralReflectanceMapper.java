package com.ydsw.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.SpectralReflectance;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* @author yanzhimeng
* //@description 针对表【spectral_reflectance(光谱反射率表)】的数据库操作Mapper
* //@createDate 2024-08-29 15:59:21
* /@Entity com.ydsw.domain.SpectralReflectance
*/
@Repository
@Mapper
public interface SpectralReflectanceMapper extends BaseMapper<SpectralReflectance> {

   List<Map<String,Object>> selectByTimeAndDevice(@Param("time") String time, @Param("device") Integer device,@Param("wavelength") Integer wavelength);
   //返回一个时间点全部数据

   List<Map<String,Object>> selectByTime(@Param("time") String time);

    List<String> selectObservationTime();

    //弃用
   @Delete("delete from spectral_reflectance where observation_time = #{time} and wavelength = #{wavelength}")
   int deleteByTimeAndWaveLength(@Param("time") String time,@Param("wavelength") String wavelength);

   IPage<Map<String,Object>> selecetSpectralReflectancPageByObservationAndOrderBywavelength(IPage<?> page,@Param("spectralReflectanceClass") SpectralReflectance spectralReflectanceClass);

   //根据用，分割的字符串id批量删除
   //@Update("update ydsw.spectral_reflectance set status = 1 where (id) in :idList ")
   void deleteById(@Param("idList")List<Integer> idList,@Param("dateSelected")String dateSelected, @Param("filepath")String filepath);

    IPage<Map<String, Object>> findDataPageByCondition(IPage<SpectralReflectance> page, @Param("time")String time,@Param("filepath") String filepath, @Param("opens")Integer opens);

    //@Select("select id,wavelength,device_id,data from ydsw.spectral_reflectance where status = 0 and observation_time = #{time}")

    int updateOpenStatusByFilepathsAndDate(@Param("idList") List<Integer> idList, @Param("dateSelected") String dateSelected, @Param("openValue") int openValue,@Param("filepath") String filepath);

    List<String> fetchObservationTimeByMonth();

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByDay();
}




