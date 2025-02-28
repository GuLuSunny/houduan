package com.ydsw.config;

import com.ydsw.handler.AnonymousAuthenticationHandler;
import com.ydsw.handler.CustomerAccessDeniedHandler;
import com.ydsw.handler.LoginFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/7/13  12:20
 * @Version 1.0
 */
@Configuration
@EnableWebSecurity
/*
 *EnableGlobalMethodSecurity开启全局校验
 * prePostEnabled=true会解锁＠PreAuthorize和@PostAuthorize两个注解，
 * ＠PreAuthorize会在执行方法前验证，@PostAuthorize会在执行方法后验证。
 *securedEnabled=true会解锁＠Secured 注解。
 * */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
    //自定义的用于认证的过滤器，进行jwt校验
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private AuthenticationConfiguration authenticationConfiguration;

    //认证用户无权限访问资源处理器
    @Autowired
    private CustomerAccessDeniedHandler customerAccessDeniedHandler;
    //客户端进行认证数据的提交，或者匿名用户访问无权限资源处理器
    @Autowired
    private AnonymousAuthenticationHandler anonymousAuthenticationHandler;
    //文件上传异常处理器
//    @Autowired
//    private FileExceptionHandler fileExceptionHandler;
    //自定义认证失败处理器
    @Autowired
    private LoginFailureHandler loginFailureHandler;

    /**
     * chain.anyMatchers(...).permitAll() ；指定哪些请求路径下不需要身份验证
     * chain.authorizeRequests()； 配置请求授权规则，如  .anyRequest().authenticated()表示任何请求都需要经过身份认证
     * .requestMatchers()；某个请求不需要拦截校验
     * .httpBasic；配置基本的http身份认证
     * .csrf()；通过csrf方法配置CSRF保护
     * .sessionManagement()；通过sessionManagement方法配置会话管理
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        List<RequestMatcher> chains = new ArrayList<>();
//        chains.add(new AntPathRequestMatcher("/api/login"));
//        chains.add(new AntPathRequestMatcher("/api/register"));
        //配置关闭csrf机制
        http.csrf().disable();
        //用户认证校验失败处理器
        http.formLogin((e) -> {
            e.failureHandler(loginFailureHandler);
        });
        //配置请求拦截方式
        //permitAll：随意访问
        http
                .authorizeHttpRequests((authz) -> authz.requestMatchers("/api/login","/api/logout","/api/register").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());
        //配置过滤器的执行顺序
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //认证用户无权限访问资源处理器
        //客户端进行认证数据的提交，
        // 或者匿名用户访问无权限资源处理器
        http.exceptionHandling((configurer) -> {
            configurer.accessDeniedHandler(customerAccessDeniedHandler);
            configurer.authenticationEntryPoint(anonymousAuthenticationHandler);
        });
        return http.build();
    }

    //网络安全配置，忽略部分路径（如静态文件路径）
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/ignore1", "/ignore2");
    }

    //创建BCryptPasswordEncoder注入容器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //登录时需要调用AuthenticationManager.authenticate执行一次校验
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
