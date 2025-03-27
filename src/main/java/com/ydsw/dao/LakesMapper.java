package com.ydsw.dao;

import com.ydsw.domain.Lakes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【lakes】的数据库操作Mapper
* @createDate 2025-03-27 15:33:10
* @Entity com.ydsw.domain.Lakes
*/
public interface LakesMapper extends BaseMapper<Lakes> {
    List<Lakes> selectLakesByConditions(@Param("lakesClass") Lakes lakesClass);
}




