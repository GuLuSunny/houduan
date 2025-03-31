package com.ydsw.dao;

import com.ydsw.domain.Sluice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【Sluice】的数据库操作Mapper
* @createDate 2025-03-17 10:56:16
* @Entity generator.domain.Sluice
*/
@Mapper
public interface SluiceMapper extends BaseMapper<Sluice> {

    List<Map<String,Object>> selectBySluice(@Param("sluiceClass") Sluice sluiceClass);
}




