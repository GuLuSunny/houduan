package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydsw.domain.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author zhang
* @description 针对表【user_role(用户表和角色表关联表)】的数据库操作Mapper
* @createDate 2024-10-28 22:48:19
* @Entity generator.domain.UserRole
*/
@Repository
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}




