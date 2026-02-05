package com.ydsw.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.ModelFileStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_file_status】的数据库操作Service
* @createDate 2025-07-21 11:15:44
*/
public interface ModelFileStatusService extends IService<ModelFileStatus> {
    void dropModelFileStatus(List<Integer> ids,  ModelFileStatus modelFileStatus);

    List<Map<String,Object>> selectUserAndFileStatus( ModelFileStatus modelFileStatus);

    void updateDealStatusViod( ModelFileStatus modelFileStatus);

    List<String> fetchObservationTime(String className);

    List<String> fetchObservationTimeByYear(String className);

    List<String> fetchObservationTimeByMonth(String className);

    IPage<Map<String, Object>> queryModelFileStatusPage(ModelFileStatus modelFileStatus);
}
