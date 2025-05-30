package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.domain.Channel;
import com.ydsw.service.ChannelService;
import com.ydsw.dao.ChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【channel】的数据库操作Service实现
* @createDate 2025-03-17 16:00:07
*/
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel>
    implements ChannelService{
    @Autowired
    private ChannelMapper channelMapper;
    @Override
    public List<Map<String,Object>> selectAllChannelByConditions(Channel channelClass)
    {
        return channelMapper.selectAllChannelByConditions(channelClass);
    }
    @Override
    public void updataTablesByTypes( String classType,String idType, List<Integer> ClassIdList,Channel channelClass)
    {
        channelMapper.updataTablesByTypes(classType,idType,ClassIdList,channelClass);
    }
}




