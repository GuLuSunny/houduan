package com.ydsw.service;

import com.ydsw.domain.PumpingStation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【pumping_station(泵站表)】的数据库操作Service
* @createDate 2025-03-27 14:48:25
*/
public interface PumpingStationService extends IService<PumpingStation> {
    List<PumpingStation> selectAllPumpingStationByCondition(PumpingStation pumpingStationClass);
}
