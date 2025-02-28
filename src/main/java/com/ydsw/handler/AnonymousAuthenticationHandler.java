package com.ydsw.handler;

import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author zhangkaifei
 * @description: 客户端进行认证数据的提交，或者匿名用户访问无权限资源处理器
 * @date 2024/8/29  17:44
 * @Version 1.0
 */
@Component
@Slf4j
public class AnonymousAuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info(authException.toString(), authException.getMessage());
        authException.printStackTrace();
        //发生这个异常访问的行为，做一个响应处理，给一个响应结果
        //设置客户端响应的内容类型
        response.setContentType("application/json;charset=utf-8");
        //获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        String result = "";
        if (authException instanceof BadCredentialsException) {//用户名登录异常，用户名不存在，authException.getMessage()为抛出的异常信息
            result = JSONUtil.toJsonStr(ResultTemplate.fail(authException.getMessage()));
        } else if (authException instanceof InternalAuthenticationServiceException) {//用户名为空异常
            result = JSONUtil.toJsonStr(ResultTemplate.fail("用户名为空！"));
        } else {//其他验证异常
            result = JSONUtil.toJsonStr(ResultTemplate.fail("匿名用户无权限访问！"));
        }
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}