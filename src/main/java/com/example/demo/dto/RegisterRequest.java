package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "角色不能为空")
    private String role; // student 或 teacher

    // 学生特有字段
    private String className;
    private String major;

    // 教师特有字段
    private String department;
}

