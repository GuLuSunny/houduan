package com.ydsw.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.ydsw.exception.CustomerAuthenticationException;
import com.ydsw.handler.LoginFailureHandler;
import com.ydsw.pojo.vo.LoginUserVo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author zhangkaifei
 * @description: Token验证过滤器，每一个servlet请求，只会执行一次
 * @date 2024/8/27  22:38
 * @Version 1.0
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();///api/login
        log.info(requestURI);
        if (requestURI.equals("/api/login") || requestURI.equals("/api/register")||requestURI.equals("/api/logout")) {//登录或者注册、注销直接放行
            filterChain.doFilter(request, response);//放行
            return;
        } else {
            try {
                this.validateToken(request);
            } catch (AuthenticationException e) {
                loginFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);//放行
    }

    /**
     * 用于token校验的方法
     *
     * @param request
     */
    private void validateToken(HttpServletRequest request) {
        //判断token是否为空
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new CustomerAuthenticationException("token 为空！");
        }
        //token是否在session中
        HttpSession session = request.getSession();
        String tokenSession = (String) session.getAttribute("Authorization");
        if (ObjectUtil.isEmpty(tokenSession) || !tokenSession.equals(token)) {
           throw new CustomerAuthenticationException("token 已过期！");
        }
        //校验令牌
        //解析
        LoginUserVo loginUserVo = null;
        try {
            JWT jwt = JWTUtil.parseToken(token);
            //获取全部载荷信息
            JWTPayload jwtPayload = jwt.getPayload();
            log.info(jwtPayload.getClaimsJson().toString());
            loginUserVo = JSONUtil.toBean((JSONObject) JSONUtil.parse(jwtPayload.getClaimsJson().get("data")), LoginUserVo.class);
        } catch (Exception e) {
            throw new CustomerAuthenticationException("token 校验失败！");
        }
        //把验证完的获取到的用户信息再次放入到springsecurity上下文中
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUserVo, null, loginUserVo.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
