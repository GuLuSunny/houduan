package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.DroneImage;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【drone_image(无人机影像)】的数据库操作Service
* @createDate 2024-11-06 16:54:02
*/
public interface DroneImageService extends IService<DroneImage> {

    IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens);

    void updateOpenStatusByFilepathsAndDate(List<Integer> filepaths, String dateSelected, int openValue, String filepath);

    IPage<Map<String, Object>> fetchDataByObservationTimeAndFilepath(int currentPage, int pageSize, DroneImage droneImageClass);

    void delByIdList(List<Integer> idList, String dateSelected, String filepath, String deviceName,String type);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();

}
