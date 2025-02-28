package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.SateliteRemoteSensingMapper;
import com.ydsw.domain.SateliteRemoteSensing;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.service.SateliteRemoteSensingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【satelite_remote_sensing(卫星遥感)】的数据库操作Service实现
* @createDate 2024-11-06 16:50:32
*/
@Service
public class SateliteRemoteSensingServiceImpl extends ServiceImpl<SateliteRemoteSensingMapper, SateliteRemoteSensing>
    implements SateliteRemoteSensingService{
    @Autowired
    private SateliteRemoteSensingMapper sateliteRemoteSensingMapper;
    @Override
    public IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return sateliteRemoteSensingMapper.findDataPageByCondition(page,time,filepath,  opens);
    }

    @Override
    public void updateOpenStatusByFilepathsAndDate(List<Integer> idList, String dateSelected, int openValue, String filepath) {
        sateliteRemoteSensingMapper.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue,filepath);
    }

    @Override
    public IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, SateliteRemoteSensing sateliteRemoteSensingClass) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return sateliteRemoteSensingMapper.getSateliteRemoteSensingDatasPage(page,sateliteRemoteSensingClass);
    }
    @Override
    public void delByIdList(List<Integer> idList, String dateSelected, String filepath, String deviceId,String type) {
        sateliteRemoteSensingMapper.deleteById(idList,dateSelected,filepath,deviceId,type);
    }

    @Override
    public List<String> fetchObservationTimeByYear() {
        return sateliteRemoteSensingMapper.fetchObservationTimeByYear();
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return sateliteRemoteSensingMapper.fetchObservationTimeByMonth();
    }
}




