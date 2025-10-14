package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.BirdMapper;
import com.ydsw.domain.Bird;
import com.ydsw.service.BirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description 针对表【bird】的数据库操作Service实现
 * @createDate 2024-11-11 12:48:45
 */
@Service
public class BirdServiceImpl extends ServiceImpl<BirdMapper, Bird> implements BirdService {

    @Autowired
    private BirdMapper birdMapper;

    @Override
    public List<Map<String,Object>> getAllBirds() {
        return birdMapper.selectAll();
    }

    @Override
    public Bird getBirdById(Integer id) {
        return birdMapper.selectById(id);
    }

//    @Override
//    public void deleteById(List<Integer> ids)
//    {
//        birdMapper.deleteById(ids);
//    }
    @Override
    public IPage<Map<String,Object>> getBirdsByPage(int offset, int limit, List<Integer> speciesIds, Integer familyId, Integer orderId) {
        IPage<Map<String,Object>> page = new Page<>(offset,limit);
        return birdMapper.selectPage(page,speciesIds,familyId,orderId);
    }
}
