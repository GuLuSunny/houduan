package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.Role;

import java.util.List;
import java.util.Map;

/**
 * @author zhang
 * @description 针对表【role(角色表)】的数据库操作Service
 * @createDate 2024-10-28 21:55:21
 */
public interface RoleService extends IService<Role> {
    IPage<Map<String, Object>> getRoleRightListPage(int currentPage, int pageSize, Role roleClass);

    void deleteRoleDataById(List<Integer> idArray);

}
