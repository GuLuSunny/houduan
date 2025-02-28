package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.SpectralReflectanceMapper;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.service.SpectralReflectanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author yanzhimeng
* //@description 针对表【spectral_reflectance(光谱反射率表)】的数据库操作Service实现
* //@createDate 2024-08-29 15:59:21
*/
@Service
public class SpectralReflectanceServiceImpl extends ServiceImpl<SpectralReflectanceMapper, SpectralReflectance>
    implements SpectralReflectanceService {
    @Autowired
    private SpectralReflectanceMapper spectralReflectanceMapper;

    @Override
    public List<Map<String, Object>> fetchDataByObservationTimeAndDevice(String time, Integer device,Integer wavelength) {
        return spectralReflectanceMapper.selectByTimeAndDevice(time, device,wavelength);
    }

    @Override
    public List<Map<String, Object>> fetchDataByObservationTime(String time) {

        return spectralReflectanceMapper.selectByTime(time);
    }

    @Override
    public List<String> fetchObservationTime() {
        return spectralReflectanceMapper.selectObservationTime();
    }

    @Override
    public int delBytimeAndWavelength(String time, String wavelength) {
        return spectralReflectanceMapper.deleteByTimeAndWaveLength(time, wavelength);
    }

    @Override
    public void delByIdList(List<Integer> idList, String dateSelected, String filepath)
    {
        spectralReflectanceMapper.deleteById(idList,dateSelected,filepath);
    }

    @Override
    public IPage<Map<String,Object>> getSpectralReflectancePageListByCondition(int currentPage, int pageSize, SpectralReflectance spectralReflectance)
    {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return spectralReflectanceMapper.selecetSpectralReflectancPageByObservationAndOrderBywavelength(page, spectralReflectance);
    }

    @Override
    public IPage<Map<String, Object>> findDataPageByCondition(int currentPage, int pageSize, String time, String filepath, Integer opens) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return spectralReflectanceMapper.findDataPageByCondition(page,time,filepath,  opens);
    }

    @Override
    public void updateOpenStatusByFilepathsAndDate(List<Integer> idList, String dateSelected, int openValue, String filepath) {
       spectralReflectanceMapper.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue,filepath);

    }

    @Override
    public List<String> fetchObservationTimeByYear() {
        return spectralReflectanceMapper.fetchObservationTimeByYear();
    }

    @Override
    public List<String> fetchObservationTimeByDay() {
        return spectralReflectanceMapper.fetchObservationTimeByDay();
    }

    @Override
    public List<String> fetchObservationTimeByMonth() {
        return spectralReflectanceMapper.fetchObservationTimeByMonth();
    }
}




