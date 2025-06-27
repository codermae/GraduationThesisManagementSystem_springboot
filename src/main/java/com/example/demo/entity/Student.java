package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Student")
public class Student {
    @TableId
    private String studentId;
    private String name;
    private String className; // class是关键字，使用className
    private String major;
    private String password;
}