package com.ydsw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.*.dao") //开启扫描MyBatis包
public class YdswApplication {

    public static void main(String[] args) {
        SpringApplication.run(YdswApplication.class, args);
    }

}
