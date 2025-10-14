package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.ObservationBird;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【observation_bird(鸟类观察调查数据表
)】的数据库操作Mapper
* @createDate 2024-11-07 20:27:23
* @Entity generator.domain.ObservationBird
*/
@Mapper
public interface ObservationBirdMapper extends BaseMapper<ObservationBird> {
    List<Map<String,Object>> selectIdFromTable(@Param("watchGroup") String watchGroup);
    List<Map<String,Object>> selectIdBySpecies(@Param("speciesName") String speciesName,@Param("familyName") String familyName, @Param("orderName") String orderName);
    IPage<Map<String,Object>> selectPage(IPage<?> page,@Param("observationBirdClass") ObservationBird observationBirdClass);
    void deleteWaterLevelByIdList(@Param("idList") List<Integer> idList,@Param("observationBirdClass") ObservationBird observationBirdClass);

    List<Map<String,Object>> selectNameById(@Param("sid") Integer sid,@Param("fid") Integer fid,@Param("oid") Integer oid);
    List<Map<String,Object>> selectGroupById(@Param("groupId") Integer groupId);

    List<Map<String,Object>> selectAllByDay(@Param("observationTime") String observationTme,@Param("observationTimeBegin") String observationTimeBegin,@Param("observationTimeEnd") String observationTimeEnd, @Param("observationPeriodBegin")String observationPeriodBegin,@Param("observationPeriodEnd")String observationPeriodEnd);

    List<Map<String,Object>> selectAllSpecies();
    List<Map<String,Object>> selectAllFamily();
    List<Map<String,Object>> selectAllOrder();
    List<Map<String,Object>> selectAllGroup();

    List<Map<String,Object>> selectMaxTimeBigin();

    List<Map<String,Object>> selectObservationTimes();
}




