package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.ModelProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_product】的数据库操作Service
* @createDate 2025-10-28 18:08:25
*/

@Service
public interface ModelProductService extends IService<ModelProduct> {
    List<Map<String, Object>> getModelProductByCondition( ModelProduct modelProduct);

    boolean updateModelProduct(List<Integer> idList,ModelProduct modelProduct);

    IPage<Map<String,Object>> getProductPageByConditions(int currentPage, int pageSize,ModelProduct modelProduct);

    List<String> fetchObservationTime();

    List<String> fetchObservationTimeByYear();

    List<String> fetchObservationTimeByMonth();
}
