package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.ModelFileStatus;
import com.ydsw.service.ModelFileStatusService;
import com.ydsw.dao.ModelFileStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_file_status】的数据库操作Service实现
* @createDate 2025-07-21 11:15:44
*/
@Service
public class ModelFileStatusServiceImpl extends ServiceImpl<ModelFileStatusMapper, ModelFileStatus>
    implements ModelFileStatusService{
    @Autowired
    private ModelFileStatusMapper modelFileStatusMapper;

    @Override
    public void dropModelFileStatus(List<Integer> ids, ModelFileStatus modelFileStatus)
    {
        modelFileStatusMapper.dropModelFileStatus(ids, modelFileStatus);
    }

    @Override
    public List<Map<String,Object>> selectUserAndFileStatus(ModelFileStatus modelFileStatus)
    {
        return modelFileStatusMapper.selectUserAndFileStatus(modelFileStatus);
    }

    @Override
    public void updateDealStatusViod( ModelFileStatus modelFileStatus)
    {
        modelFileStatusMapper.updateDealStatusViod(modelFileStatus);
    }
}




