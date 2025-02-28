package com.ydsw.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author zhangkaifei
 * @description: 关于Token自定义验证异常类
 * @date 2024/8/29  16:56
 * @Version 1.0
 */
public class CustomerAuthenticationException extends AuthenticationException {
    public CustomerAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CustomerAuthenticationException(String msg) {
        super(msg);
    }
}
