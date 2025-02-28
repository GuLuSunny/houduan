package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.RoleMapper;
import com.ydsw.domain.Role;
import com.ydsw.domain.SpectralReflectance;
import com.ydsw.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhang
 * @description 针对表【role(角色表)】的数据库操作Service实现
 * @createDate 2024-10-28 21:55:21
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public IPage<Map<String, Object>> getRoleRightListPage(int currentPage, int pageSize, Role roleClass) {
        IPage<SpectralReflectance> page = new Page<>(currentPage, pageSize);
        return roleMapper.getRoleRightListPage(page, roleClass);
    }

    @Override
    public void deleteRoleDataById(List<Integer> idArray) {
        roleMapper.deleteRoleDataById(idArray);
    }

}




