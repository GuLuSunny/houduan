package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.ModelProduct;
import com.ydsw.service.ModelProductService;
import com.ydsw.dao.ModelProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_product】的数据库操作Service实现
* @createDate 2025-10-28 18:08:25
*/
@Service
public class ModelProductServiceImpl extends ServiceImpl<ModelProductMapper, ModelProduct>
    implements ModelProductService{

    @Autowired
    private ModelProductMapper modelProductMapper;
    @Override
    public List<Map<String, Object>> getModelProductByCondition(List<Integer> idList,ModelProduct modelProduct)
    {
        return baseMapper.getModelProductByCondition(idList,modelProduct);
    }

    @Override
    public boolean updateModelProduct(List<Integer> idList,ModelProduct modelProduct)
    {
        return modelProductMapper.updateModelProduct(idList,modelProduct);
    }

    @Override
    public List<String> fetchObservationTime()
    {
        return modelProductMapper.selectObservationTime();
    }

    @Override
    public List<String> fetchObservationTimeByYear()
    {
        return modelProductMapper.fetchObservationTimeByYear();
    }

    @Override
    public List<String> fetchObservationTimeByMonth()
    {
        return modelProductMapper.fetchObservationTimeByMonth();
    }
    @Override
    public IPage<Map<String,Object>> getProductPageByConditions(int currentPage, int pageSize, ModelProduct modelProduct)
    {
        IPage<Map<String,Object>> page = new Page<>(currentPage, pageSize);
        return modelProductMapper.getProductPageByConditions(page,modelProduct);
    }
//新的我的
    @Override
    public List<Map<String, Object>> getModelProductWithSort(String sortType, String sortOrder)
    {
        return modelProductMapper.getModelProductWithSort(sortType, sortOrder);
    }
}






