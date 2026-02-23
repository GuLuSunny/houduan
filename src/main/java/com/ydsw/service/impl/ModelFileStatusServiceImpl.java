package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.ModelFileStatus;
import com.ydsw.service.ModelFileStatusService;
import com.ydsw.dao.ModelFileStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

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

    @Override
    public List<String> fetchObservationTime(String className)
    {
        return modelFileStatusMapper.selectObservationTime(className);
    }

    @Override
    public List<String> fetchObservationTimeByYear(String className){
        return modelFileStatusMapper.fetchObservationTimeByYear(className);
    }


    @Override
    public List<String> fetchObservationTimeByMonth(String className)
    {
        return modelFileStatusMapper.fetchObservationTimeByMonth(className);
    }


    @Override
    public IPage<Map<String, Object>> queryModelFileStatusPage(ModelFileStatus modelFileStatus) {
        // 构造分页对象，使用实体类中带默认值的分页参数
        Page<Map<String, Object>> page = new Page<>(
                modelFileStatus.getPageNum(),
                modelFileStatus.getPageSize()
        );
        // 调用Mapper层分页方法
        return modelFileStatusMapper.queryModelFileStatusPage(page, modelFileStatus);
    }

    @Override
    public boolean updateByCondition(ModelFileStatus updateEntity, ModelFileStatus condition) {
        // 构建更新条件，使用 UpdateWrapper
        UpdateWrapper<ModelFileStatus> wrapper = new UpdateWrapper<>();

        // 只对非空字段添加条件，适配数据库字段名
        if (condition.getId() != null) wrapper.eq("id", condition.getId());
        if (condition.getClassName() != null) wrapper.eq("class_name", condition.getClassName());
        if (condition.getUserName() != null) wrapper.eq("user_name", condition.getUserName());
        if (condition.getDealStatus() != null) wrapper.eq("deal_status", condition.getDealStatus());
        if (condition.getType() != null) wrapper.eq("type", condition.getType());
        if (condition.getObservationTime() != null) wrapper.eq("observation_time", condition.getObservationTime());
        if (condition.getModelName() != null) wrapper.eq("model_name", condition.getModelName());
        if (condition.getStartTime() != null) wrapper.eq("start_time", condition.getStartTime());
        if (condition.getEndTime() != null) wrapper.eq("end_time", condition.getEndTime());

        // 只更新未删除的记录
        wrapper.eq("status", 0);

        // 第一个参数是要更新的实体（非null字段会被更新），第二个参数是更新条件
        return this.update(updateEntity, wrapper);
    }
}




