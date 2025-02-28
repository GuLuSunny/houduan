package com.ydsw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/7/15  22:20
 * @Version 1.0
 */
@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        //new一个CorsConfiguration对象用于CORS配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许所有域的请求
        corsConfiguration.addAllowedOriginPattern("*");
        //允许请求携带认证信息（cookies）
        corsConfiguration.setAllowCredentials(true);
        //允许所有的请求方法
        corsConfiguration.addAllowedMethod("*");
        //允许所有的请求头
        corsConfiguration.addAllowedHeader("*");
        //允许暴露所有头部信息
        corsConfiguration.addExposedHeader("*");

        //添加映射路径
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        //返回新的CorsFilter对象
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
