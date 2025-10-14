package com.ydsw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.LandUseArea;

import java.util.List;

/**
* @author Administrator
* @description 针对表【land_use_area(陆浑水库周边土地利用现状面积统计表)】的数据库操作Service
* @createDate 2024-09-10 20:05:12
*/
public interface LandUseAreaService extends IService<LandUseArea> {
    List<LandUseArea> fetchLandUseAreasByidOrName(Integer id,String name);
}
