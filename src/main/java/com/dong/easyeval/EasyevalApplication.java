package com.dong.easyeval;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dong.easyeval.mapper")
public class EasyevalApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyevalApplication.class, args);
    }

}
