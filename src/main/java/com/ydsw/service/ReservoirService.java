package com.ydsw.service;

import com.ydsw.domain.Reservoir;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【reservoir】的数据库操作Service
* @createDate 2025-03-27 15:14:12
*/
public interface ReservoirService extends IService<Reservoir> {
    List<Reservoir> selectReservoirByConditons(Reservoir reservoirClass);
}
