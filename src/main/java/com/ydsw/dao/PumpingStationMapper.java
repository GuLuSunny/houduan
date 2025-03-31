package com.ydsw.dao;

import com.ydsw.domain.PumpingStation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【pumping_station(泵站表)】的数据库操作Mapper
* @createDate 2025-03-27 14:48:25
* @Entity com.ydsw.domain.PumpingStation
*/
@Mapper
public interface PumpingStationMapper extends BaseMapper<PumpingStation> {
    List<Map<String,Object>> selectAllPumpingStationByCondition(@Param("pumpingStationClass") PumpingStation pumpingStationClass);
}




