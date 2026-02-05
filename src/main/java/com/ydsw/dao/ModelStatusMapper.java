package com.ydsw.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.ModelList;
import com.ydsw.domain.ModelStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_status(模型使用状态 每人/每模型 对应一条)】的数据库操作Mapper
* @createDate 2025-07-10 16:10:28
* @Entity com.ydsw.domain.ModelStatus
*/
@Mapper
public interface ModelStatusMapper extends BaseMapper<ModelStatus> {
    List<Map<String,Object>> selectModelStatusByConditions(@Param("modelStatusClass")ModelStatus modelStatusClass);

    void updateModelStatus(@Param("modelStatusClass")ModelStatus modelStatus);

    void dropModelLogs(@Param("idList") List<Integer> idList,@Param("modelStatusClass")ModelStatus modelStatus);

    List<Map<String, Object>> getModelStatusPageByConditions(IPage<?> page, @Param("modelStatus") ModelStatus modelStatus);
}




