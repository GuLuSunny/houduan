package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.Lakes;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【lakes】的数据库操作Service
* @createDate 2025-03-27 15:33:10
*/
public interface LakesService extends IService<Lakes> {
    List<Lakes> selectLakesByConditions( Lakes lakesClass);
    IPage<Map<String,Object>> selectLakesPageByConditions(int current,int pagesize, Lakes lakesClass);
}
