package com.ydsw.dao;

import com.ydsw.domain.Forest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;
/**
* @author Administrator
* @description 针对表【forest】的数据库操作Mapper
* @createDate 2025-11-01 16:22:19
* @Entity com.ydsw.domain.Forest
*/
public interface ForestMapper extends BaseMapper<Forest> {
    @Select("SELECT ST_AsGeoJSON(geom)::json as geometry, gid FROM forest")
    List<Map<String, Object>> getAllForestAsGeoJson();
}




