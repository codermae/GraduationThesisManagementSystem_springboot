// StudentGradeResponse.java - 学生成绩响应
package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StudentGradeResponse {
    private String studentId;
    private String studentName;
    private String className;
    private String major;
    private String topicTitle;
    private String teacherName;
    private BigDecimal thesisScore;
    private String thesisRemarks;
    private BigDecimal defenseScore;
    private String defenseComments;
    private BigDecimal finalScore;
    private LocalDateTime calculatedAt;
}