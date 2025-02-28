package com.ydsw.handler;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fengwenyi.api.result.ResultTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author zhangkaifei
 * @description: 认证用户无权限访问资源处理器
 * @date 2024/8/29  17:01
 * @Version 1.0
 */
@Component
@Slf4j
public class CustomerAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info(accessDeniedException.toString(), accessDeniedException.getMessage());
        accessDeniedException.printStackTrace();
        //发生这个无权限访问的行为，做一个响应处理，给一个响应结果
        //设置客户端响应的内容类型
        response.setContentType("application/json;charset=utf-8");
        //获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //返回结果序列化
        String result = JSONUtil.toJsonStr(ResultTemplate.fail("无权限访问，请联系管理员！"));
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}