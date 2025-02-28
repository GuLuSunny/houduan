package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.TbFlowMapper;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.domain.TbFlow;
import com.ydsw.service.TbFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author lenovo
 * //@description 针对表【tb_flow(伊河流量登记表)】的数据库操作Service实现
 * //@createDate 2024-08-24 17:04:03
 */
@Service
public class TbFlowServiceImpl extends ServiceImpl<TbFlowMapper, TbFlow>
        implements TbFlowService {
    @Autowired
    private TbFlowMapper tbFlowMapper;


    @Override
    public List<String> fetchObservationTimeByYear() {
        return tbFlowMapper.fetchObservationTimeByYear("");
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return tbFlowMapper.fetchObservationTimeByMonth("");
    }

    @Override
    public List<Map<String, String>> fetchFlowByTimePeriodAndDevice(String timeEarliest, String timeLatest, String device) {
        return tbFlowMapper.selectFlowByObservationTime2(timeEarliest, timeLatest, device);
    }
    @Override
    public Integer getDeviceIdByDeviceName(String deviceName) {
        return tbFlowMapper.getDeviceIdByDeviceName(deviceName);
    }

    /**
     * 通过年份和设备号进行查询
     * @param year
     * @param deviceId
     * @return
     */
    @Override
    public List<Map<String, Object>> findFlowByYearAndDevice(String year, String deviceId) {
        return tbFlowMapper.findFlowByYearAndDevice( year, deviceId);
    }
    /**
     * 通过年-月和设备号进行查询
     * @param yearMonth
     * @param deviceId
     * @return
     */
    @Override
    public List<Map<String, Object>> findFlowByYearMonthAndDevice(String yearMonth, String deviceId) {
        return tbFlowMapper.findFlowByYearMonthAndDevice( yearMonth, deviceId);
    }

    @Override
    public IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, TbFlow tbFlowClass) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return tbFlowMapper.getTbFlowDatasPage(page,tbFlowClass);
    }

    @Override
    public void delByIdList(List<Integer> idList, String dateSelected, String filepath, String deviceId) {
        tbFlowMapper.deleteById(idList,dateSelected,filepath,deviceId);
    }

    /**
     * 根据 条件进行分页
     * @param currentPage
     * @param pageSize
     * @param time
     * @param filepath
     * @param opens
     * @return
     */
    @Override
    public IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return tbFlowMapper.findDataPageByCondition(page,time,filepath,  opens);
    }

    @Override
    public void updateOpenStatusByFilepathsAndDate(List<Integer> idList, String dateSelected, int openValue, String filepath) {
        tbFlowMapper.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue,filepath);

    }

    @Override
    public List<String> fetchObservationTimeByYear1(String filepath) {
        return  tbFlowMapper.fetchObservationTimeByYear(filepath);
    }

    @Override
    public List<String> fetchObservationTimeByMonth1(String filepath) {
        return  tbFlowMapper.fetchObservationTimeByMonth(filepath);
    }
    @Override
    public List<Map<String,Object>> selectFlowByObservationTimeAndDeviceId( String observationTime,Integer deviceId)
    {
        return tbFlowMapper.selectFlowByObservationTimeAndDeviceId(observationTime,deviceId);
    }
}