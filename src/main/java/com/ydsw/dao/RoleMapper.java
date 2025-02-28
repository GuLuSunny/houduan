package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zhang
 * @description 针对表【role(角色表)】的数据库操作Mapper
 * @createDate 2024-10-28 21:55:21
 * @Entity com.ydsw.domain.Role
 */
@Repository
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    IPage<Map<String, Object>> getRoleRightListPage(IPage<?> page, @Param("roleClass") Role roleClass);

    void deleteRoleDataById(@Param("idList")List<Integer> idArray);

}




