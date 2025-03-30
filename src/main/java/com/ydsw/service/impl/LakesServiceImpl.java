package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.Lakes;
import com.ydsw.service.LakesService;
import com.ydsw.dao.LakesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【lakes】的数据库操作Service实现
* @createDate 2025-03-27 15:33:10
*/
@Service
public class LakesServiceImpl extends ServiceImpl<LakesMapper, Lakes>
    implements LakesService{
    @Autowired
    private LakesMapper lakesMapper;
    @Override
    public List<Lakes> selectLakesByConditions(Lakes lakesClass)
    {
        return lakesMapper.selectLakesByConditions(lakesClass);
    }
    @Override
    public IPage<Map<String,Object>> selectLakesPageByConditions(int current, int pagesize, Lakes lakesClass)
    {
        IPage<Map<String,Object>> page = new Page<>(current,pagesize);
        return lakesMapper.selectLakesPageByConditions(page,lakesClass);
    }
}




