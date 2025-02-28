package com.ydsw.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.River0Arc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【river0_arc】的数据库操作Mapper
* @createDate 2025-01-18 22:14:49
* @Entity com.ydsw.domain.River0Arc
*/
@Mapper
public interface River0ArcMapper extends BaseMapper<River0Arc> {
    List<Map<String,Object>> selectPagesByRiverClass( @Param("River0ArcClass") River0Arc river0Arc);
}




