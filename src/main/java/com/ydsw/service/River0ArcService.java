package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.River0Arc;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【river0_arc】的数据库操作Service
* @createDate 2025-01-18 22:14:49
*/
public interface River0ArcService extends IService<River0Arc> {
    List<Map<String,Object>> selectPagesByRiverClass(River0Arc river0Arc);
}
