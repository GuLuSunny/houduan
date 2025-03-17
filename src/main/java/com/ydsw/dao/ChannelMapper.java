package com.ydsw.dao;

import com.ydsw.domain.Channel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【channel】的数据库操作Mapper
* @createDate 2025-03-17 16:00:07
* @Entity com.ydsw.domain.Channel
*/
@Mapper
public interface ChannelMapper extends BaseMapper<Channel> {
    List<Channel> selectAllChannelByConditions(@Param("channelClass") Channel channelClass);
}




