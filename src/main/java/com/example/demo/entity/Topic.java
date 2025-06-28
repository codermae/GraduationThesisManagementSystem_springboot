package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private String teacherName;
    private String departmentName;
}