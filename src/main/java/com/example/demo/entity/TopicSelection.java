package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("TopicSelection")
public class TopicSelection {
    @TableId
    private String studentId;
    private String topicId;
    private String teacherId;
    private LocalDate selectionDate;

    // 关联查询时使用
    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String topicTitle;
    @TableField(exist = false)
    private String teacherName;
}