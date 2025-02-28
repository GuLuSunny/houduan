package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.SpectralReflectance;

import java.util.List;
import java.util.Map;

/**
* @author yanzhimeng
* //@description 针对表【spectral_reflectance(光谱反射率表)】的数据库操作Service
* //@createDate 2024-08-29 15:59:21
*/
public interface SpectralReflectanceService extends IService<SpectralReflectance> {

    List<Map<String,Object>> fetchDataByObservationTimeAndDevice(String time, Integer device,Integer wavelength);
    List<Map<String,Object>> fetchDataByObservationTime(String time);

    List<String> fetchObservationTime();

    int delBytimeAndWavelength(String time, String wavelength);

    /**
     *
     * @param idList id列表
     * @param dateSelected   日期
     * @param filepath  文件名
     */
    void delByIdList(List<Integer> idList, String dateSelected, String filepath);
    //通用分页
    IPage<Map<String,Object>> getSpectralReflectancePageListByCondition(int currentPage, int pageSize,SpectralReflectance spectralReflectance);

    IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens);

    void updateOpenStatusByFilepathsAndDate(List<Integer> filepaths, String dateSelected, int openValue, String filepath);

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByDay();

    List<String> fetchObservationTimeByMonth();
}
