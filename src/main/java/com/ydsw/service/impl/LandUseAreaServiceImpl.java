package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.LandUseAreaMapper;
import com.ydsw.domain.LandUseArea;
import com.ydsw.service.LandUseAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【land_use_area(陆浑水库周边土地利用现状面积统计表)】的数据库操作Service实现
* @createDate 2024-09-10 20:05:12
*/
@Service
public class LandUseAreaServiceImpl extends ServiceImpl<LandUseAreaMapper, LandUseArea>
    implements LandUseAreaService{

    @Autowired
    private LandUseAreaMapper landUseAreaMapper;

    @Override
    public List<LandUseArea> fetchLandUseAreasByidOrName(Integer id,String name)
    {
        return landUseAreaMapper.selectLandUseAreasById(id,name);
    }
}




