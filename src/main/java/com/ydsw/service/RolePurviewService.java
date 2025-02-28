package com.ydsw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.RolePurview;

import java.util.List;

/**
* @author zhang
* @description 针对表【role_purview(角色表和权限表关联表)】的数据库操作Service
* @createDate 2024-10-29 17:12:07
*/
public interface RolePurviewService extends IService<RolePurview> {

    void deleteRoleDataById(List<Integer> idArray);
}
