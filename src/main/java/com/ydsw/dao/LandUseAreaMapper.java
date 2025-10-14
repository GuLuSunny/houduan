package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydsw.domain.LandUseArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【land_use_area(陆浑水库周边土地利用现状面积统计表)】的数据库操作Mapper
* @createDate 2024-09-10 20:05:12
* @Entity generator.domain.LandUseArea
*/
@Mapper
public interface LandUseAreaMapper extends BaseMapper<LandUseArea> {

    @Select("select * from land_use_area where land_use_id = #{id} or land_name = #{name}")
    List<LandUseArea> selectLandUseAreasById(@Param("id") Integer id,@Param("name") String name);
}




