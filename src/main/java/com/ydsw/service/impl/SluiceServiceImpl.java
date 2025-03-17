package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.SluiceMapper;
import com.ydsw.domain.Sluice;
import com.ydsw.service.SluiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【Sluice】的数据库操作Service实现
 * @createDate 2025-03-17 10:56:16
 */
@Service
public class SluiceServiceImpl extends ServiceImpl<SluiceMapper, Sluice>
        implements SluiceService {
    @Autowired
    private SluiceMapper sluiceMapper;
    @Override
    public List<Sluice> selectBySluice(Sluice sluiceClass)
    {
        return sluiceMapper.selectBySluice(sluiceClass);
    }
}
