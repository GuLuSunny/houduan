package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.ReservoirMapper;
import com.ydsw.domain.Reservoir;
import com.ydsw.service.ReservoirService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【reservoir】的数据库操作Service实现
* @createDate 2025-03-27 15:14:12
*/
@Service
public class ReservoirServiceImpl extends ServiceImpl<ReservoirMapper, Reservoir>
    implements ReservoirService{
    @Autowired
    private ReservoirMapper reservoirMapper;
    @Override
    public List<Reservoir> selectReservoirByConditons(Reservoir reservoirClass)
    {
        return reservoirMapper.selectReservoirByConditons(reservoirClass);
    }
}




