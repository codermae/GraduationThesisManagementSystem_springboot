// GradeStatisticsResponse.java - 成绩统计响应
package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GradeStatisticsResponse {
    private Long totalStudents;
    private Long gradedStudents;
    private Long ungradedStudents;
    private BigDecimal averageScore;
    private BigDecimal maxScore;
    private BigDecimal minScore;
    private Long excellentCount; // 优秀(>=90)
    private Long goodCount; // 良好(80-89)
    private Long passCount; // 及格(60-79)
    private Long failCount; // 不及格(<60)
}