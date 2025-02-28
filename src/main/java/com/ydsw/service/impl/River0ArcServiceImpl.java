package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fengwenyi.api.result.PageRequestVo;
import com.ydsw.domain.River0Arc;
import com.ydsw.service.River0ArcService;
import com.ydsw.dao.River0ArcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【river0_arc】的数据库操作Service实现
* @createDate 2025-01-18 22:14:49
*/
@Service
public class River0ArcServiceImpl extends ServiceImpl<River0ArcMapper, River0Arc>
    implements River0ArcService{
    @Autowired
    private River0ArcMapper river0ArcMapper;
    @Override
    public List<Map<String,Object>> selectPagesByRiverClass(River0Arc river0Arc)
    {
        //IPage<Map<String,Object>> page = new Page<>(currentPage,pageSize);
        return river0ArcMapper.selectPagesByRiverClass(river0Arc);
    }
}




