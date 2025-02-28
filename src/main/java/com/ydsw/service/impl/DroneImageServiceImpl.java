package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.DroneImageMapper;
import com.ydsw.domain.DroneImage;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.service.DroneImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【drone_image(无人机影像)】的数据库操作Service实现
* @createDate 2024-11-06 16:54:02
*/
@Service
public class DroneImageServiceImpl extends ServiceImpl<DroneImageMapper, DroneImage>
    implements DroneImageService{
    @Autowired
    private DroneImageMapper droneImageMapper;
    @Override
    public IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return droneImageMapper.findDataPageByCondition(page,time,filepath,  opens);
    }

    @Override
    public void updateOpenStatusByFilepathsAndDate(List<Integer> filepaths, String dateSelected, int openValue, String filepath) {
        droneImageMapper.updateOpenStatusByFilepathsAndDate(filepaths, dateSelected, openValue,filepath);
    }

    @Override
    public IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, DroneImage droneImageClass) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return droneImageMapper.getDroneImageDatasPage(page,droneImageClass);
    }
    @Override
    public void delByIdList(List<Integer> idList, String dateSelected, String filepath, String deviceName,String type) {
        droneImageMapper.deleteById(idList,dateSelected,filepath,deviceName,type);
    }

    @Override
    public List<String> fetchObservationTimeByYear() {
        return droneImageMapper.fetchObservationTimeByYear();
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return droneImageMapper.fetchObservationTimeByMonth();
    }
}




