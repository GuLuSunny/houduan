package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.Forest;
import com.ydsw.service.ForestService;
import com.ydsw.dao.ForestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
/**
* @author Administrator
* @description 针对表【forest】的数据库操作Service实现
* @createDate 2025-11-01 16:22:19
*/
@Service
public class ForestServiceImpl extends ServiceImpl<ForestMapper, Forest>
        implements ForestService{

    @Autowired
    private ForestMapper forestMapper;

    @Override
    public List<Map<String, Object>> getAllForestAsGeoJson() {
        return forestMapper.getAllForestAsGeoJson();
    }
}



