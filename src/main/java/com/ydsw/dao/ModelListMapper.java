package com.ydsw.dao;

import com.ydsw.domain.ModelList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_list】的数据库操作Mapper
* @createDate 2025-07-10 16:06:56
* @Entity com.ydsw.domain.ModelList
*/
@Mapper
public interface ModelListMapper extends BaseMapper<ModelList> {
    List<Map<String,Object>> getAllModelByClassName(@Param("className") String className);
    List<String> getAllClassName();
}




