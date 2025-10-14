package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.OrderMapper;
import com.ydsw.domain.Order;
import com.ydsw.service.OrderService;
import org.springframework.stereotype.Service;

/**
* @author zhang
* @description 针对表【order(目表)】的数据库操作Service实现
* @createDate 2024-11-21 16:47:39
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

}




