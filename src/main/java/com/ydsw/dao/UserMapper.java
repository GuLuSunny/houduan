package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zhang
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2024-08-27 10:44:55
 * @Entity generator.domain.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据条件分页查询
     *
     * @param page
     * @param userClass
     * @return
     */

    IPage<Map<String,Object>> selectUserPageByCondition(IPage<?> page, @Param("userClass") User userClass);

    List<Map<String,Object>> selectUserByCondition(@Param("userClass") User userClass);
    void deleteUserListsByCondition(@Param("userIds")   List<String> userIds, @Param("userClass")  User userClass);

    void updateUserInfo(@Param("userClass")User userClass);
}




