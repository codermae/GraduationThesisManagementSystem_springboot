package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Teacher")
public class Teacher {
    @TableId
    private String teacherId;
    private String name;
    private String department;
    private String password;
}