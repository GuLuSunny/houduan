package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydsw.domain.Order;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhang
* @description 针对表【order(目表)】的数据库操作Mapper
* @createDate 2024-11-21 16:47:39
* @Entity generator.domain.Order
*/
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}




