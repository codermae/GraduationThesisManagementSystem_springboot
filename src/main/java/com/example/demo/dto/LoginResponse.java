package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String userId;
    private String name;
    private String role;
    private String token;

    // 学生信息
    private String className;
    private String major;

    // 教师信息
    private String department;
}