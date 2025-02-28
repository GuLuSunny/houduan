package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.User;
import com.ydsw.domain.UserRole;
import com.ydsw.service.UserRoleService;
import com.ydsw.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
@RequestMapping(value = "/api/user")
public class UserController {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;

    /**
     * 根据搜索条件获取用户列表，分页
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_user_getUserListByCondition')")
    @PostMapping(value = "/getUserListByCondition")
    @ResponseBody
    public ResultTemplate<Object> getUserListByCondition(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
//        log.info("currentPage:{},pageSize:{},Condition:{}",currentPage,pageSize,jsonObject.toString());
        User userClass = JSONUtil.toBean(jsonObject, User.class);
        // 获取用户列表
        // 分页
        // 返回结果
        IPage<Map<String, Object>> userIPage = userService.getUserListByCondition(currentPage, pageSize, userClass);
        return ResultTemplate.success(userIPage);
    }

    /**
     * 根据用户id集合和条件删除用户列表
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_user_deleteUserList')")
    @PostMapping(value = "/deleteUserList")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> deleteUserList(@RequestBody JSONObject jsonObject) {
        List<String> userIds = jsonObject.getBeanList("userIds", String.class);
        log.info("Condition:{}", jsonObject.toString());
        //userIds为空，这搜索条件不能为空，以搜索条件进行删除
        // userIds不为空，以userIds为条件进行删除
        if (userIds == null || userIds.size() == 0) {
            User userClass = JSONUtil.toBean(jsonObject, User.class);
            if (userClass != null && (!"".equals(userClass.getUsername()) || !"".equals(userClass.getTel()) || !"".equals(userClass.getProductionCompany()))) {
                userService.deleteUserList(userIds, userClass);
            } else {
                return ResultTemplate.fail("删除失败，请选择用户");
            }
        } else {
            userService.deleteUserList(userIds, new User());
        }
        return ResultTemplate.success();
    }

    /**
     * 根据用户id进行更新信息
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_user_updateUserInfo')")
    @PostMapping(value = "/updateUserInfo")
    @ResponseBody
    @Transactional//事务
    public ResultTemplate<Object> updateUserInfo(@RequestBody JSONObject jsonObject) {
        User userClass = JSONUtil.toBean(jsonObject, User.class);
        if (userClass.getPassword() != null && !"".equals(userClass.getPassword().trim())) {
            //加密
            BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
            String digestHex =bcryptPasswordEncoder.encode(userClass.getPassword().trim());
//            Digester digester = DigestUtil.digester("sm3");
//            String digestHex = digester.digestHex(userClass.getPassword());
            userClass.setPassword(digestHex);
        }
        userService.updateUserInfo(userClass);//更新用户信息
        Map map = new HashMap();
        map.put("user_id", userClass.getId());
        userRoleService.removeByMap(map);//根据 用户id 删除用户角色表中的数据
        List<Integer> roleIds = jsonObject.getBeanList("roleId", Integer.class);
        List<UserRole> userRoleList = new ArrayList<>();
        for (Integer roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userClass.getId());
            userRole.setRoleId(roleId);
            userRoleList.add(userRole);
        }
        userRoleService.saveOrUpdateBatch(userRoleList);
        return ResultTemplate.success();
    }
}
