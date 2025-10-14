package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.ObservationBird;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【observation_bird(鸟类观察调查数据表
)】的数据库操作Service
* @createDate 2024-11-07 20:27:23
*/
public interface ObservationBirdService extends IService<ObservationBird> {
    List<Map<String,Object>> selectIdByGroup(String group);
    List<Map<String,Object>> selectIdBySpecies(String speciesName,String familyName,String orderName);

    IPage<Map<String,Object>> selectPage(int currentPage, int pageSize, ObservationBird observationBirdClass);
    void delByIdList(List<Integer> idList,ObservationBird observationBirdClass);
    List<Map<String,Object>> selectNameById( Integer sid,Integer fid, Integer oid);
    List<Map<String,Object>> selectGroupById(Integer groupId);
    List<Map<String,Object>> selectAllByDay(String observationTme,String observationTimeBegin, String observationTimeEnd,String observationPeriodBegin,String observationPeriodEnd);
    List<Map<String,Object>> selectAllSpecies();
    List<Map<String,Object>> selectAllFamily();
    List<Map<String,Object>> selectAllOrder();
    List<Map<String,Object>> selectAllGroup();

    List<Map<String,Object>> selectMaxTimeBigin();

    List<Map<String,Object>> selectObservationTimes();
}
