package com.ydsw.service;

import com.ydsw.domain.Sluice;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【Sluice】的数据库操作Service
* @createDate 2025-03-17 10:56:16
*/
public interface SluiceService extends IService<Sluice> {

    List<Sluice> selectBySluice(Sluice sluiceClass);
}
