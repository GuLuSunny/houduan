package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.PumpingStation;
import com.ydsw.service.PumpingStationService;
import com.ydsw.dao.PumpingStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【pumping_station(泵站表)】的数据库操作Service实现
* @createDate 2025-03-27 14:48:25
*/
@Service
public class PumpingStationServiceImpl extends ServiceImpl<PumpingStationMapper, PumpingStation>
    implements PumpingStationService{
    @Autowired
    private PumpingStationMapper pumpingStationMapper;

    @Override
    public List<PumpingStation> selectAllPumpingStationByCondition(PumpingStation pumpingStationClass)
    {
        return pumpingStationMapper.selectAllPumpingStationByCondition(pumpingStationClass);
    }
}




