package com.ydsw.handler;

import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.exception.CustomerAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author zhangkaifei
 * @description: 自定义认证失败处理器
 * @date 2024/8/29  20:47
 * @Version 1.0
 */
@Component
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info(exception.toString(), exception.getMessage());
        exception.printStackTrace();
        //发生这个异常访问的行为，做一个响应处理，给一个响应结果
        //设置客户端响应的内容类型
        response.setContentType("application/json;charset=utf-8");
        //获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        String result = "";
        //判断异常类型
        if (exception instanceof AccountExpiredException) {
            result = "账户过期，登录失败！";
        } else if (exception instanceof BadCredentialsException) {
            result = "用户名或密码错误，登录失败！";
        } else if (exception instanceof CredentialsExpiredException) {
            result = "密码过期，登录失败！";
        } else if (exception instanceof DisabledException) {
            result = "账户被禁用，登录失败！";
        } else if (exception instanceof LockedException) {
            result = "账户被锁，登录失败！";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            result = "账户不存在，登录失败！";
        } else if (exception instanceof CustomerAuthenticationException) {
            //自定义校验异常
            result = exception.getMessage();
        } else {
            result = "登录失败！";
        }
        result = JSONUtil.toJsonStr(ResultTemplate.fail(result));
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
