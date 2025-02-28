package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.UserMapper;
import com.ydsw.domain.User;
import com.ydsw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhang
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-08-27 10:44:55
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean isExistsByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.exists(wrapper);
    }

    @Override
    public IPage<Map<String,Object>> getUserListByCondition(int currentPage, int pageSize, User userClass) {
        // 创建分页对象
        IPage<User> page = new Page<>(currentPage, pageSize);
        return userMapper.selectUserPageByCondition(page, userClass);
    }

    @Override
    public void deleteUserList(List<String> userIds,User userClass) {
         userMapper.deleteUserListsByCondition(userIds,userClass);
    }

    @Override
    public void updateUserInfo(User userClass) {
        userMapper.updateUserInfo(userClass);
    }

    @Override
    public List<Map<String,Object>> selectUserByCondition(User userClass)
    {
        return userMapper.selectUserByCondition(userClass);
    }

}




