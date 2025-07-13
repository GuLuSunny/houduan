package com.ydsw.service;

import com.ydsw.domain.ModelList;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【model_list】的数据库操作Service
* @createDate 2025-07-10 16:06:56
*/
public interface ModelListService extends IService<ModelList> {
    List<Map<String,Object>> getAllModelByClassName( String className);

    List<String> getAllClassName();
}
