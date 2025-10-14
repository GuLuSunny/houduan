package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.Bird;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @description 针对表【bird】的数据库操作Mapper
 */
@Mapper
public interface BirdMapper extends BaseMapper<Bird> {

    List<Map<String,Object>> selectAll();

    IPage<Map<String,Object>> selectPage(IPage<?> page,@Param("speciesIds")List<Integer> speciesIds,@Param("familyId") Integer familyId,@Param("orderId") Integer orderId);
}



