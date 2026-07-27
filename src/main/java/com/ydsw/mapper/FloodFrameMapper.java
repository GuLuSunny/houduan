package com.ydsw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydsw.pojo.FloodFrame;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FloodFrameMapper extends BaseMapper<FloodFrame> {
}