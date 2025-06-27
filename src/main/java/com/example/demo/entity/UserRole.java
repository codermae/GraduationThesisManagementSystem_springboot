package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("UserRole")
public class UserRole {
    @TableId
    private String userId;
    private String role; // student 或 teacher
}