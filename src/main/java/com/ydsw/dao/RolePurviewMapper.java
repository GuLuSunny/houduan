package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydsw.domain.RolePurview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zhang
* @description 针对表【role_purview(角色表和权限表关联表)】的数据库操作Mapper
* @createDate 2024-10-29 17:12:07
* @Entity generator.domain.RolePurview
*/
@Mapper
public interface RolePurviewMapper extends BaseMapper<RolePurview> {

    void deleteRoleDataById(@Param("idList")List<Integer> idArray);
}




