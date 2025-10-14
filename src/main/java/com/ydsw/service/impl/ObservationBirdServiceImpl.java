package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.ObservationBirdMapper;
import com.ydsw.domain.ObservationBird;
import com.ydsw.service.ObservationBirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【observation_bird(鸟类观察调查数据表
)】的数据库操作Service实现
* @createDate 2024-11-07 20:27:23
*/
@Service
public class ObservationBirdServiceImpl extends ServiceImpl<ObservationBirdMapper, ObservationBird>
    implements ObservationBirdService{
    @Autowired
    private ObservationBirdMapper observationBirdMapper;

    @Override
    public List<Map<String,Object>> selectIdByGroup(String group)
    {
        return observationBirdMapper.selectIdFromTable(group);
    }

    @Override
    public List<Map<String,Object>> selectIdBySpecies(String speciesName,String familyName, String orderName)
    {
        return observationBirdMapper.selectIdBySpecies(speciesName,familyName,orderName);
    }

    @Override
    public IPage<Map<String,Object>> selectPage(int currentPage, int pageSize, ObservationBird observationBirdClass)
    {
        IPage<Map<String,Object>> page = new Page<>(currentPage,pageSize);
        return observationBirdMapper.selectPage(page,observationBirdClass);
    }
    @Override
    public void delByIdList(List<Integer> idList,ObservationBird observationBirdClass) {
        observationBirdMapper.deleteWaterLevelByIdList(idList,observationBirdClass);
    }
    @Override
    public List<Map<String,Object>> selectNameById( Integer sid,Integer fid, Integer oid)
    {
        return observationBirdMapper.selectNameById(sid,fid,oid);
    }
    @Override
    public   List<Map<String,Object>> selectGroupById(Integer groupId)
    {
        return observationBirdMapper.selectGroupById(groupId);
    }
    @Override
    public List<Map<String,Object>> selectAllByDay(String observationTme,String observationTimeBegin, String observationTimeEnd,String observationPeriodBegin,String observationPeriodEnd)
    {
        return observationBirdMapper.selectAllByDay(observationTme,observationTimeBegin,observationTimeEnd,observationPeriodBegin,observationPeriodEnd);
    }
    @Override
    public List<Map<String,Object>> selectAllSpecies()
    {
        return observationBirdMapper.selectAllSpecies();
    }
    @Override
    public List<Map<String,Object>> selectAllFamily(){
        return observationBirdMapper.selectAllFamily();
    }
    @Override
    public List<Map<String,Object>> selectAllOrder(){
        return observationBirdMapper.selectAllOrder();
    }
    @Override
    public List<Map<String,Object>> selectAllGroup()
    {
        return observationBirdMapper.selectAllGroup();
    }
    @Override
    public List<Map<String,Object>> selectMaxTimeBigin()
    {
        return observationBirdMapper.selectMaxTimeBigin();
    }
    @Override
    public List<Map<String,Object>> selectObservationTimes()
    {
        return observationBirdMapper.selectObservationTimes();
    }
}




