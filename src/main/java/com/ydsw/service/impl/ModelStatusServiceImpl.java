package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.ModelStatus;
import com.ydsw.service.ModelStatusService;
import com.ydsw.dao.ModelStatusMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_status(模型使用状态 每人/每模型 对应一条)】的数据库操作Service实现
* @createDate 2025-07-10 16:10:28
*/
@Service
public class ModelStatusServiceImpl extends ServiceImpl<ModelStatusMapper, ModelStatus>
    implements ModelStatusService{

    @Autowired
    private ModelStatusMapper modelStatusMapper;
    public List<Map<String,Object>> selectModelStatusByConditions(ModelStatus modelStatusClass)
    {
        return modelStatusMapper.selectModelStatusByConditions(modelStatusClass);
    }

    public void updateModelStatus(ModelStatus modelStatus)
    {
        modelStatusMapper.updateModelStatus(modelStatus);
    }

    public void dropModelLogs(List<Integer> idList,ModelStatus modelStatus)
    {
        modelStatusMapper.dropModelLogs(idList,modelStatus);
    }
}




