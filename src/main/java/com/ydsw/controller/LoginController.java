package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.User;
import com.ydsw.domain.UserRole;
import com.ydsw.pojo.vo.LoginUserVo;
import com.ydsw.service.UserRoleService;
import com.ydsw.service.UserService;
import com.ydsw.utils.ConstantUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangkaifei
 * //@description: TODO
 * //@date 2024/7/15  21:01
 * //@Version 1.0
 */
@Slf4j
@Controller
public class LoginController {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 登录
     */
    @PostMapping(value = "/api/login")
    @ResponseBody
    public ResultTemplate<Object> checklogin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        log.info(JSONUtil.toJsonStr(jsonObject));
        User user = JSONUtil.toBean(jsonObject, User.class);
        //将登录的用户名和密码封装成UsernamePasswordAuthenticationToken对象
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        //通过authenticationManager.authenticate方法进行用户认证
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        //认证不通过，返回自定义异常
        if (authentication == null) {
            return ResultTemplate.fail("登录失败！");
        }
        //认证成功，就从authentication中的getPrincipal方法中拿到认证通过后的登录用户对象
        LoginUserVo loginUserVo = (LoginUserVo) authentication.getPrincipal();
        //生成令牌
        String token = createToken(JSONUtil.toJsonStr(loginUserVo));
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        User u = loginUserVo.getUser();
//        u.setId(null);
        u.setPassword(null);
        u.setCreateTime(null);
        u.setStatus(null);
        map.put("userinfo", JSONUtil.toJsonStr(u));
        map.put("principal", JSONUtil.parse(loginUserVo.getList()));
        //token存储在session中
        HttpSession session = request.getSession();
        session.setAttribute("Authorization", token);
        session.setMaxInactiveInterval((int) (ConstantUtils.JWT_TTL / 1000));//秒
//        if ("admin".equals(u.getUsername())) {
//            session.setMaxInactiveInterval(Integer.MAX_VALUE);
//        }
        return ResultTemplate.success(map);
    }

    /**
     * 注册
     * //@param jsonObject
     * //@return
     */
    @PostMapping(value = "/api/register")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> register(@RequestBody JSONObject jsonObject) {
        User user = JSONUtil.toBean(jsonObject, User.class);
        user.setStatus(0);
        List<Map<String, Object>> userList = userService.selectUserByCondition(user);
        if (userList!=null&& !userList.isEmpty()) {
            return ResultTemplate.fail("用户已存在！");
        }
        //加密
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        String digestHex = bcryptPasswordEncoder.encode(user.getPassword().trim());
        user.setPassword(digestHex);
        userService.save(user);
        if ((!jsonObject.containsKey("roleId")) || jsonObject.get("roleId") == null || jsonObject.getJSONArray("roleId").size() == 0) {
            return ResultTemplate.success("用户注册成功！");
        }
        List<Integer> roleIds = jsonObject.getBeanList("roleId", Integer.class);
        List<UserRole> userRoleList = new ArrayList<>();
        for (Integer roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRoleList.add(userRole);
        }
        userRoleService.saveOrUpdateBatch(userRoleList);
        return ResultTemplate.success("用户名注册成功！");
    }

    /**
     * 注销
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/api/logout")
    @ResponseBody
    public ResultTemplate logout(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            //清除上下文
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            //清除session
            HttpSession session = request.getSession();
            session.removeAttribute("Authorization");
        }
        return ResultTemplate.success("退成成功！");
    }

    private String createToken(String claims) {
        //获取当前时间
        Long now = System.currentTimeMillis();
        //获取一小时之后的时间
        Long newTime = now + ConstantUtils.JWT_TTL;
        //使用Map集合来存储载荷信息、有效期
        Map<String, Object> payload = new HashMap<>();
        //签发时间
        payload.put(JWTPayload.ISSUED_AT, now);
        //过期时间
        payload.put(JWTPayload.EXPIRES_AT, newTime);
        //生效时间
        payload.put(JWTPayload.NOT_BEFORE, now);
        //负载
        payload.put("data", claims);
        //默认HS256签名算法
        String token = JWTUtil.createToken(payload, ConstantUtils.JWT_KEY.getBytes());
        return token;
    }
}
