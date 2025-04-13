package com.ydsw.dao;

import com.ydsw.domain.Reservoir;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【reservoir】的数据库操作Mapper
* @createDate 2025-03-27 15:14:12
* @Entity com.ydsw.domain.Reservoir
*/
@Mapper
public interface ReservoirMapper extends BaseMapper<Reservoir> {
    List<Map<String,Object>> selectReservoirByConditons(@Param("reservoirClass")Reservoir reservoirClass);
}




