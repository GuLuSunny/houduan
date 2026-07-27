package com.ydsw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ydsw.mapper")
public class YdswApplication {
    public static void main(String[] args) {
        SpringApplication.run(YdswApplication.class, args);
    }
}
