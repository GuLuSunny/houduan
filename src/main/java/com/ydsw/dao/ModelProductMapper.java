package com.ydsw.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.ModelProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_product】的数据库操作Mapper
* @createDate 2025-10-28 18:08:25
* @Entity com.ydsw.domain.ModelProduct
*/

@Mapper
public interface ModelProductMapper extends BaseMapper<ModelProduct> {

    List<Map<String, Object>> getModelProductByCondition(@Param("idList") List<Integer> idList,@Param("modelProduct") ModelProduct modelProduct);

    boolean updateModelProduct(@Param("idList") List<Integer> idList,@Param("modelProduct") ModelProduct modelProduct);

    IPage<Map<String,Object>> getProductPageByConditions(IPage<?> page,@Param("modelProduct") ModelProduct modelProduct);
    List<String> selectObservationTime();

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();
}




