package com.ydsw.service;

import com.ydsw.domain.ModelStatus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_status(模型使用状态 每人/每模型 对应一条)】的数据库操作Service
* @createDate 2025-07-10 16:10:28
*/
public interface ModelStatusService extends IService<ModelStatus> {
    List<Map<String,Object>> selectModelStatusByConditions(ModelStatus modelStatusClass);

    void updateModelStatus(ModelStatus modelStatus);

    void dropModelLogs(List<Integer> idList,ModelStatus modelStatus);

    List<Map<String, Object>> getModelStatusPageByConditions(int pageNum, int pageSize, ModelStatus modelStatus);

    boolean deleteModelStatusByIdList(List<Integer> idList, Date updateTime);

    boolean deleteModelStatusByCondition(ModelStatus condition);

    boolean updateModelStatusById(ModelStatus updateEntity);

    boolean updateModelStatusByCondition(ModelStatus updateEntity, ModelStatus condition);
}
