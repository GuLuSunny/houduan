package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.UserRoleMapper;
import com.ydsw.domain.UserRole;
import com.ydsw.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
* @author zhang
* @description 针对表【user_role(用户表和角色表关联表)】的数据库操作Service实现
* @createDate 2024-10-28 22:48:19
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService{

}




