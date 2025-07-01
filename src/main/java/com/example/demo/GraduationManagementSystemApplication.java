package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@MapperScan("com.example.demo.mapper")
@EnableTransactionManagement
public class GraduationManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduationManagementSystemApplication.class, args);
        System.out.println("===============================================");
        System.out.println("毕业设计文档资料管理系统启动成功！");
        System.out.println("访问地址：http://localhost:8080");
        System.out.println("API文档：http://localhost:8080/api/auth/validate");
        System.out.println("===============================================");
    }
}