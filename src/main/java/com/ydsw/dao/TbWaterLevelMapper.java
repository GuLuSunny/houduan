package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.TbWaterLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zhang
 * //@description 针对表【tb_water_level(陆浑水库逐日库水位登记表)】的数据库操作Mapper
 * //@createDate 2024-07-26 16:09:18
 * //@Entity generator.domain.TbWaterLevel
 */
@Mapper
public interface TbWaterLevelMapper extends BaseMapper<TbWaterLevel> {
    //根据具体时间查询
    List<Map<String, Object>> selectWaterLevelByObservationTime(@Param("time") String observationTime,@Param("deviceId") Integer deviceId);

    //http://localhost:8090/api/waterlevel2?timeEarliest=2015-12-02&timeLatest=2017-01-02
    List<Map<String, Object>> selectWaterLevelByObservationTime2(@Param("timeEarliest") String timeEarliest, @Param("timeLatest") String timeLatest,@Param("deviceId") Integer deviceId);

    void deleteWaterLevelByIdList(@Param("idList") List<Integer> idList, @Param("observationTime") String observationTime, @Param("filepath") String filepath);

    IPage<Map<String, Object>> selectWaterLevelPageByObservationTime(IPage<?> page, @Param("tbWaterLevelClass") TbWaterLevel tbWaterLevelClass);

    List<String> selectObservationTimeByYear(@Param("filepath")String filepath);

    List<String> selectObservationTimeByMonth(@Param("filepath")String filepath);

    IPage<Map<String, Object>> findDataPageByCondition(IPage<SpectralReflectance> page, @Param("time")String time,@Param("filepath") String filepath, @Param("opens")Integer opens);

    int updateOpenStatusByFilepathsAndDate(@Param("idList") List<Integer> idList, @Param("dateSelected") String dateSelected, @Param("openValue") int openValue,@Param("filepath") String filepath);

}




