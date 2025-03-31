package com.ydsw.service;

import com.ydsw.domain.Channel;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【channel】的数据库操作Service
* @createDate 2025-03-17 16:00:07
*/
public interface ChannelService extends IService<Channel> {
    List<Map<String,Object>> selectAllChannelByConditions(Channel channelClass);
}
