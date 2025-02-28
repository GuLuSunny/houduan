package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Role;
import com.ydsw.domain.RolePurview;
import com.ydsw.service.RolePurviewService;
import com.ydsw.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/10/18  15:51
 * @Version 1.0
 */
@Slf4j
@Controller
@RequestMapping(value = "/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RolePurviewService rolePurviewService;

    /**
     * 查询角色列表
     *
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_role_getRoleList')")
    @PostMapping(value = "/getRoleList")
    @ResponseBody
    public ResultTemplate<Object> getRoleList() {
        List<Role> roleList = roleService.list();
        return ResultTemplate.success(roleList);
    }

    /**
     * 查询角色-权限分页列表
     *
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_role_getRoleRightListPage')")
    @PostMapping(value = "/getRoleRightListPage")
    @ResponseBody
    public ResultTemplate<Object> getRoleRightListPage(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        Role roleClass = JSONUtil.toBean(jsonObject, Role.class);
        IPage<Map<String, Object>> rolePage = roleService.getRoleRightListPage(currentPage, pageSize, roleClass);
        return ResultTemplate.success(rolePage);
    }

    /*
     * 根据id列表删除
     * */
    @PreAuthorize("hasAnyAuthority('api_role_deleteRoleDataById')")
    @RequestMapping(value = "/deleteRoleDataById")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> deleteRoleDataById(@RequestBody JSONObject jsonObject) {
        List<Integer> idArray = jsonObject.getBeanList("ids", Integer.class);//id列表
        roleService.deleteRoleDataById(idArray);
        rolePurviewService.deleteRoleDataById(idArray);
        return ResultTemplate.success("删除成功！");
    }

    /**
     * 根据角色id进行更新信息
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_role_updateRoleInfo')")
    @PostMapping(value = "/updateRoleInfo")
    @ResponseBody
    @Transactional//事务
    public ResultTemplate<Object> updateRoleInfo(@RequestBody JSONObject jsonObject) {
        String roleInfo = jsonObject.getStr("roleInfo");
        Integer roleId = jsonObject.getInt("roleId");
        String roleEng = jsonObject.getStr("roleEng");
        Role role = new Role();
        role.setId(roleId);
        role.setRoleEng(roleEng);
        role.setRoleinfo(roleInfo);
        roleService.updateById(role);//更新角色信息
        Map map = new HashMap();
        map.put("role_id", roleId);
        rolePurviewService.removeByMap(map);//根据 角色id 删除角色权限表中的数据
        List<Integer> rightIds = jsonObject.getBeanList("rightId", Integer.class);
        List<RolePurview> rolePurviewList = new ArrayList<>();
        for (Integer rightId : rightIds) {
            RolePurview rolePurview = new RolePurview();
            rolePurview.setRoleId(roleId);
            rolePurview.setPurviewId(rightId);
            rolePurviewList.add(rolePurview);
        }
        rolePurviewService.saveOrUpdateBatch(rolePurviewList);
        return ResultTemplate.success();
    }

    /**
     * 新增角色信息
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_role_addRoleInfo')")
    @PostMapping(value = "/addRoleInfo")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> addRoleInfo(@RequestBody JSONObject jsonObject) {
        String roleInfo = jsonObject.getStr("roleInfo");
        String roleEng = jsonObject.getStr("roleEng");
        Role role = new Role();
        role.setRoleEng(roleEng);
        role.setRoleinfo(roleInfo);
        //角色名称校验，唯一性
        Map map1=new HashMap();
        map1.put("roleinfo",roleInfo);
        List<Role> roleList1=roleService.listByMap(map1);
        if(roleList1!=null&&roleList1.size()>0){
            return ResultTemplate.fail("角色名称已存在，请重新输入！");
        }
        //角色标识符校验，唯一性
        Map map2=new HashMap();
        map2.put("role_eng",roleEng);
        List<Role> roleList2=roleService.listByMap(map2);
        if(roleList2!=null&&roleList2.size()>0){
            return ResultTemplate.fail("角色标识符已存在，请重新输入！");
        }
        roleService.save(role);//保存角色信息
        Integer roleId = role.getId();//获取角色id
        List<Integer> rightIds = jsonObject.getBeanList("rightId", Integer.class);
        List<RolePurview> rolePurviewList = new ArrayList<>();
        for (Integer rightId : rightIds) {
            RolePurview rolePurview = new RolePurview();
            rolePurview.setRoleId(roleId);
            rolePurview.setPurviewId(rightId);
            rolePurviewList.add(rolePurview);
        }
        rolePurviewService.saveOrUpdateBatch(rolePurviewList);
        return ResultTemplate.success();
    }
}
