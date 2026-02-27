package com.ydsw.dao;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.ModelFileStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_file_status】的数据库操作Mapper
* @createDate 2025-07-21 11:15:44
* @Entity com.ydsw.domain.ModelFileStatus
*/
@Mapper
public interface ModelFileStatusMapper extends BaseMapper<ModelFileStatus> {
    void dropModelFileStatus(@Param("idList")List<Integer> ids,@Param("modelFileStatusClass") ModelFileStatus modelFileStatus);

    List<Map<String,Object>> selectUserAndFileStatus(@Param("modelFileStatusClass") ModelFileStatus modelFileStatus);

    void updateDealStatusViod(@Param("modelFileStatusClass") ModelFileStatus modelFileStatus);

    List<String> selectObservationTime(@Param("className") String className);

    List<String> fetchObservationTimeByYear(@Param("className") String className);

    List<String> fetchObservationTimeByMonth(@Param("className") String className);

    IPage<Map<String, Object>> queryModelFileStatusPage(
            IPage<Map<String, Object>> page,
            @Param("modelFileStatus") ModelFileStatus modelFileStatus
    );
    int updateByCondition(
            @Param("updateEntity") ModelFileStatus updateEntity,
            @Param("condition") ModelFileStatus condition
    );
}





