package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

@Data
@TableName("Topic")
public class Topic {
    @TableId
    private String topicId;
    private String title;
    private String teacherId;
    private String difficulty;
    private String direction;
    private String content;

    // 关联查询时使用
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String departmentName;
}