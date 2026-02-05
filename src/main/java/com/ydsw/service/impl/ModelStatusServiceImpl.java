package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 分页查询遥感产品生产记录和状态
     * @param pageNum 当前页码
     * @param pageSize 每页展示多少条数据
     * @param modelStatus 查询条件入参
     * @return 结果集合
     */
    @Override
    public List<Map<String, Object>> getModelStatusPageByConditions(int pageNum, int pageSize, ModelStatus modelStatus) {
        IPage<Map<String,Object>> page = new Page<>(pageNum,pageSize);
        return modelStatusMapper.getModelStatusPageByConditions(page, modelStatus);
    }
}




