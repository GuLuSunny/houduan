package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.ModelList;
import com.ydsw.service.ModelListService;
import com.ydsw.dao.ModelListMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_list】的数据库操作Service实现
* @createDate 2025-07-10 16:06:56
*/
@Service
public class ModelListServiceImpl extends ServiceImpl<ModelListMapper, ModelList>
    implements ModelListService{

    @Autowired
    private ModelListMapper modelListMapper;

    @Override
    public List<Map<String,Object>> getAllModelByClassName(String className)
    {
        return modelListMapper.getAllModelByClassName(className);
    }
    @Override
    public List<String> getAllClassName()
    {
        return modelListMapper.getAllClassName();
    }
}




