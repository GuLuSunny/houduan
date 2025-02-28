package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ydsw.dao.UserMapper;
import com.ydsw.domain.User;
import com.ydsw.pojo.vo.LoginUserVo;
import com.ydsw.service.PurviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/8/27  17:41
 * @Version 1.0
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PurviewService rightService;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        //根据名称查询账号信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = new User();
        user.setUsername(username);
        user.setStatus(0);
        List<Map<String,Object>> userList=userMapper.selectUserByCondition(user);
        if (userList == null||userList.size()==0) {
            throw new UsernameNotFoundException("不存在此用户！");
        }
        //重写构造
        user =new User(userList.get(0));
        //授权操作,动态数据
        List<String> list = rightService.selectRightENGByUserId(user.getId());
        log.info(user.getId().toString(), list.toString());
        //返回UserDetails对象
        return new LoginUserVo(user, list);
    }
}
