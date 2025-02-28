package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.RolePurviewMapper;
import com.ydsw.domain.RolePurview;
import com.ydsw.service.RolePurviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author zhang
* @description 针对表【role_purview(角色表和权限表关联表)】的数据库操作Service实现
* @createDate 2024-10-29 17:12:07
*/
@Service
public class RolePurviewServiceImpl extends ServiceImpl<RolePurviewMapper, RolePurview>
    implements RolePurviewService{
@Autowired
private  RolePurviewMapper rolePurviewMapper;
    @Override
    public void deleteRoleDataById(List<Integer> idArray) {
        rolePurviewMapper.deleteRoleDataById(idArray);
    }
}




