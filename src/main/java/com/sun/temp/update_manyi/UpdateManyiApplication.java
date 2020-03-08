package com.sun.temp.update_manyi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.sun.temp.update_manyi.repository")
public class UpdateManyiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpdateManyiApplication.class, args);
    }

}
