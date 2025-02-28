package com.ydsw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.UserRole;
import org.springframework.stereotype.Service;

/**
* @author zhang
* @description 针对表【user_role(用户表和角色表关联表)】的数据库操作Service
* @createDate 2024-10-28 22:48:19
*/
@Service
public interface UserRoleService extends IService<UserRole> {

}
