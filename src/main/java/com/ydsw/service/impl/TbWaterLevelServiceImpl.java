package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.TbWaterLevelMapper;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.TbWaterLevel;
import com.ydsw.service.TbWaterLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhang
 * //@description 针对表【tb_water_level(陆浑水库逐日库水位登记表)】的数据库操作Service实现
 * //@createDate 2024-07-26 16:09:18
 */
@Service
public class TbWaterLevelServiceImpl extends ServiceImpl<TbWaterLevelMapper, TbWaterLevel>
        implements TbWaterLevelService {
    @Autowired
    private TbWaterLevelMapper tbWaterLevelMapper;

    @Override
    public List<Map<String, Object>> findWaterLevelByObservationTime(String observationTime,Integer deviceId) {
        return tbWaterLevelMapper.selectWaterLevelByObservationTime(observationTime,deviceId);
    }

    @Override
    public List<Map<String, Object>> fetchWaterLevelByTimePeriod(String timeEarliest, String timeLatest,Integer deviceId) {
        return tbWaterLevelMapper.selectWaterLevelByObservationTime2(timeEarliest, timeLatest, deviceId);
    }

    @Override
    public void delByIdList(List<Integer> idList, String observationTime, String filepath) {
        tbWaterLevelMapper.deleteWaterLevelByIdList(idList,observationTime, filepath);
    }

    @Override
    public IPage<Map<String, Object>> getWaterLevelByPage(int pageNo, int pageSize, TbWaterLevel tbWaterLevel) {
        IPage<Map<String, Object>> page = new Page<>(pageNo, pageSize);
        return tbWaterLevelMapper.selectWaterLevelPageByObservationTime(page, tbWaterLevel);
    }


    @Override
    public List<String> fetchObservationTimeByYear() {
        return tbWaterLevelMapper.selectObservationTimeByYear("");
    }

    @Override
    public List<String> fetchObservationTimeByYear1(String filepath) {
        return tbWaterLevelMapper.selectObservationTimeByYear(filepath);
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return tbWaterLevelMapper.selectObservationTimeByMonth("");
    }

    @Override
    public IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return tbWaterLevelMapper.findDataPageByCondition(page,time,filepath,  opens);
    }

    @Override
    public void updateOpenStatusByFilepathsAndDate(List<Integer> idList, String dateSelected, int openValue, String filepath) {
        tbWaterLevelMapper.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue,filepath);

    }

    @Override
    public List<String> fetchObservationTimeByMonth1(String filepath) {
        return tbWaterLevelMapper.selectObservationTimeByMonth(filepath);
    }
}




